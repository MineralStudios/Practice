package gg.mineral.practice.events

import gg.mineral.practice.arena.Arena
import gg.mineral.practice.contest.Contest
import gg.mineral.practice.duel.DuelSettings
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.managers.ContestManager.registerContest
import gg.mineral.practice.managers.ContestManager.remove
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.api.collection.GlueList
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.arena.EventArena
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.EventManager
import gg.mineral.practice.managers.ProfileManager
import gg.mineral.practice.match.EventMatch
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.traits.Spectatable
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.math.Countdown
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit

open class Event(
    private val hostName: String,
    open val waitTime: Int = 60,
    override val maxPlayers: Int = 24,
    override val matchData: MatchData =
        MatchData(DuelSettings(null, GametypeManager.gametypes.values.filter { it?.event == true }.randomOrNull()))
) : Contest(hostName, false),
    Spectatable {
    override val spectators: ProfileList = ProfileList()
    val arena: EventArena
        get() {
            matchData.arenaId = matchData.gametype!!.eventArenaId
            return (ArenaManager.arenas.get(matchData.arenaId) as? EventArena) ?: error("Arena not found")
        }
    final override val world = arena.generate()

    override fun addPlayer(p: Profile): Boolean {
        if (participants.size >= maxPlayers) return p.message(ErrorMessages.EVENT_FULL).let { true }
        if (started) return p.message(ErrorMessages.EVENT_STARTED).let { true }
        arena.waitingLocation.bukkit(world)
            ?.let { PlayerUtil.teleport(p, it) }
            ?: error("Arena not found")
        p.contest = this
        return participants.add(p)
            .also { broadcast(participants, ChatMessages.JOINED_EVENT.clone().replace("%player%", p.name)) }
    }

    override fun removePlayer(p: Profile) {
        if (!participants.remove(p)) return
        broadcast(participants, ChatMessages.LEFT_EVENT.clone().replace("%player%", p.name))
        p.contest = null
        if (!started) return
        if (participants.isEmpty()) {
            for (spectator in spectators) spectator.stopSpectating()
            onContestEnd()
            remove(this)
        } else if (participants.size == 1) {
            val winner = participants.first
            winner?.contest = null
            for (spectator in spectators) spectator.stopSpectating()
            onContestWin(winner)
            broadcast(ChatMessages.WON_EVENT.clone().replace("%player%", winner!!.name))
        }
    }

    override fun startContest() {
        if (started) return
        registerContest(this)

        val countdown = Countdown(waitTime, this) { time ->
            if (time % 30 != 0) return@Countdown
            broadcast(ChatMessages.EVENT_BROADCAST)
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

            if (this is AutomatedEvent)
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

    override fun createMatch(p1: Profile, p2: Profile) = EventMatch(p1, p2, matchData, this)

    override fun onContestWin(winner: Profile?) {
        winner?.contest = null
        ended = true
        remove(this)
    }

    override fun onContestEnd() {
        for (spectator in spectators) spectator.stopSpectating()
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
            for (spectator in spectators) spectator.stopSpectating()
            winner?.message(ErrorMessages.EVENT_NOT_ENOUGH_PLAYERS)
            onContestEnd()
        } else startRound()
    }

    override fun onCountdownStart(profile: Profile) {
    }
}
