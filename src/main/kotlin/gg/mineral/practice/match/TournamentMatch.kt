package gg.mineral.practice.match

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.MatchManager.remove
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.match.data.MatchStatisticCollector
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard
import gg.mineral.practice.tournaments.Tournament
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.Strings
import gg.mineral.practice.util.messages.impl.TextComponents
import org.bukkit.Bukkit

class TournamentMatch(profile1: Profile?, profile2: Profile?, matchData: MatchData, var tournament: Tournament) :
    Match(matchData, profile1, profile2) {
    override fun end(attacker: Profile, victim: Profile) {
        stat(attacker) { collector: MatchStatisticCollector -> collector.end(true) }
        stat(victim) { collector: MatchStatisticCollector -> collector.end(false) }

        deathAnimation(attacker, victim)

        stat(attacker) { this.setInventoryStats(it) }
        stat(victim) { this.setInventoryStats(it) }

        val winMessage = getWinMessage(attacker)
        val loseMessage = getLoseMessage(victim)

        for (profile in participants) {
            profile.player?.sendMessage(CC.SEPARATOR)
            profile.player?.sendMessage(Strings.MATCH_RESULTS)
            profile.player?.spigot()?.sendMessage(winMessage, TextComponents.SPLITTER, loseMessage)
            profile.player?.sendMessage(CC.SEPARATOR)
        }

        resetPearlCooldown(attacker, victim)
        attacker.scoreboard = MatchEndScoreboard.INSTANCE
        victim.scoreboard = DefaultScoreboard.INSTANCE
        if (!remove(this)) return

        victim.player?.heal()
        victim.player?.removePotionEffects()
        sendBackToLobby(victim)
        victim.contest = null

        Bukkit.getServer().scheduler.runTaskLater(PracticePlugin.INSTANCE, {
            attacker.scoreboard = DefaultScoreboard.INSTANCE
            tournament.removeMatch(this@TournamentMatch)

            attacker.teleportToLobby()

            if (!tournament.ended && tournament.participants.contains(attacker)) {
                attacker.playerStatus = PlayerStatus.IDLE
                attacker.inventory.setInventoryForTournament()
            } else attacker.inventory.setInventoryForLobby()
            if (attacker.match == this) attacker.match = null
        }, POST_MATCH_TIME.toLong())

        for (spectator in spectators) {
            spectator.player?.sendMessage(CC.SEPARATOR)
            spectator.player?.sendMessage(Strings.MATCH_RESULTS)
            spectator.player?.spigot()?.sendMessage(winMessage, TextComponents.SPLITTER, loseMessage)
            spectator.player?.sendMessage(CC.SEPARATOR)
            spectator.stopSpectating()
        }

        cleanup()
    }
}
