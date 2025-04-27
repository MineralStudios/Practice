package gg.mineral.practice.tournaments

import gg.mineral.practice.contest.Contest
import gg.mineral.practice.duel.DuelSettings
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ContestManager.registerContest
import gg.mineral.practice.managers.ContestManager.remove
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.practice.match.TournamentMatch
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.util.math.Countdown
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit

open class Tournament(
    private val hostName: String,
    open val waitTime: Int = 60,
    override val maxPlayers: Int = 24,
    override val matchData: MatchData = MatchData(DuelSettings(null, GametypeManager.gametypes.values.randomOrNull()))
) :
    Contest(hostName) {

    override fun addPlayer(p: Profile): Boolean {
        if (participants.size >= maxPlayers) return p.message(ErrorMessages.TOURNAMENT_FULL).let { true }
        if (started) return p.message(ErrorMessages.TOURNAMENT_STARTED).let { true }
        p.contest = this
        return participants.add(p)
            .also { broadcast(participants, ChatMessages.JOINED_TOURNAMENT.clone().replace("%player%", p.name)) }
    }

    override fun removePlayer(p: Profile) {
        if (!participants.remove(p)) return
        broadcast(participants, ChatMessages.LEFT_TOURNAMENT.clone().replace("%player%", p.name))
        p.contest = null
        if (!started) return
        if (participants.isEmpty()) {
            onContestEnd()
            remove(this)
        } else if (participants.size == 1) {
            val winner = participants.first
            winner?.contest = null
            onContestWin(winner)
            broadcast(ChatMessages.WON_TOURNAMENT.clone().replace("%player%", winner!!.name))
        }
    }

    override fun startContest() {
        if (started) return
        if (registerContest(this) != null) return

        val countdown = Countdown(waitTime, this) { time ->
            if (time % 30 != 0) return@Countdown
            broadcast(ChatMessages.TOURNAMENT_BROADCAST)
            Bukkit.getPlayer(hostName)?.let {
                broadcast(
                    ChatMessages.CONTEST_HOST.clone().replace(
                        "%host%",
                        hostName
                    )
                )
            }

            matchData.gametype?.name?.let {
                broadcast(
                    ChatMessages.CONTEST_MODE.clone().replace(
                        "%mode%",
                        it
                    )
                )
            }

            broadcast(
                ChatMessages.CONTEST_PLAYERS.clone().replace(
                    "%players%",
                    participants.size.toString() + "/" + maxPlayers
                )
            )

            if (this is AutomatedTournament)
                broadcast(ChatMessages.CONTEST_REWARD.clone().replace("%rank%", reward.rank))

            fun formatSeconds(seconds: Int): String {
                val minutes = seconds / 60
                val remainingSeconds = seconds % 60
                return "${minutes}m ${remainingSeconds}s"
            }

            broadcast(
                ChatMessages.CONTEST_STARTS_IN.clone().replace(
                    "%time%",
                    formatSeconds(time)
                )
            )

            broadcast(
                ChatMessages.CONTEST_JOIN.clone().setTextEvent(
                    ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join $hostName"),
                    ChatMessages.CLICK_TO_JOIN
                )
            )
        }
        countdown.start()
    }

    override fun createMatch(p1: Profile, p2: Profile) = TournamentMatch(p1, p2, matchData, this)

    override fun onContestWin(winner: Profile?) {
        winner?.contest = null
        ended = true
        remove(this)
    }

    override fun onContestEnd() {
        ended = true
        remove(this)
    }

    override fun onStart(profile: Profile) {
    }

    override fun onStart() {
        started = true
        if (participants.size == 1) {
            val winner = participants.first
            winner?.contest = null
            winner?.message(ErrorMessages.TOURNAMENT_NOT_ENOUGH_PLAYERS)
            onContestEnd()
        } else startRound()
    }

    override fun onCountdownStart(profile: Profile) {
    }
}
