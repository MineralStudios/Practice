package gg.mineral.practice.tournaments

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.practice.managers.TournamentManager.registerTournament
import gg.mineral.practice.managers.TournamentManager.remove
import gg.mineral.practice.match.Match
import gg.mineral.practice.match.TournamentMatch
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.Bukkit

class Tournament(p: Profile) {
    private var matches: GlueList<Match> = GlueList()
    var players: ProfileList = ProfileList()
    private var started: Boolean = false
    var ended: Boolean = false
    private var matchData: MatchData = MatchData(p.duelSettings)
    private var round: Int = 1
    val host: String = p.name

    init {
        addPlayer(p)
    }

    fun addPlayer(p: Profile): Boolean {
        if (started) return p.message(ErrorMessages.TOURNAMENT_STARTED).let { true }

        p.tournament = this

        val joinedMessage = ChatMessages.JOINED_TOURNAMENT.clone().replace("%player%", p.name)
        broadcast(players, joinedMessage)
        return players.add(p)
    }

    fun removePlayer(p: Profile) {
        if (!players.remove(p)) return
        p.tournament = null

        val leftMessage = ChatMessages.LEFT_TOURNAMENT.clone().replace("%player%", p.name)
        broadcast(players, leftMessage)

        if (players.isEmpty()) {
            remove(this)
            ended = true
            return
        }

        if (started && players.size == 1) {
            val winner = players.first
            winner?.tournament = null

            remove(this)
            ended = true

            val wonMessage = ChatMessages.WON_TOURNAMENT.clone().replace("%player%", winner!!.name)

            broadcast(wonMessage)
        }
    }

    fun start() {
        if (started) return

        registerTournament(this)

        val messageToBroadcast = ChatMessages.BROADCAST_TOURNAMENT.clone()
            .replace("%player%", host).setTextEvent(
                ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/join $host"
                ),
                ChatMessages.CLICK_TO_JOIN
            )

        broadcast(messageToBroadcast)

        Bukkit.getServer().scheduler.runTaskLater(PracticePlugin.INSTANCE, {
            started = true

            if (players.size == 1) {
                val winner = players.first
                winner?.tournament = null

                ErrorMessages.TOURNAMENT_NOT_ENOUGH_PLAYERS.send(winner!!.player)
                remove(this@Tournament)
                ended = true
            } else startRound()
        }, 600)
    }

    private fun startRound() {
        if (players.size == 1) {
            val winner = players.first
            winner?.tournament = null
            ended = true
            remove(this)
            return
        }

        val iter: Iterator<Profile> = players.iterator()

        while (iter.hasNext()) {
            val profile1 = iter.next()

            if (!iter.hasNext()) return profile1.message(ChatMessages.NO_OPPONENT)

            val profile2 = iter.next()
            val match = TournamentMatch(profile1, profile2, matchData, this)
            match.start()
            matches.add(match)
        }
    }

    fun removeMatch(m: Match) {
        matches.remove(m)

        if (ended) return

        if (matches.isEmpty()) {
            broadcast(players, ChatMessages.ROUND_OVER.clone().replace("%round%", "" + round))

            Bukkit.getServer().scheduler.runTaskLater(PracticePlugin.INSTANCE, {
                startRound()
                round++
            }, 100)
        }
    }
}
