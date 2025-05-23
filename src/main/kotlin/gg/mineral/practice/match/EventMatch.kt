package gg.mineral.practice.match

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.events.Event
import gg.mineral.practice.managers.MatchManager.remove
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.Strings
import gg.mineral.practice.util.messages.impl.TextComponents
import org.bukkit.Bukkit

class EventMatch(profile1: Profile?, profile2: Profile?, matchData: MatchData, private val event: Event) :
    Match(matchData, profile1, profile2) {
    override fun generateWorld() = event.world

    override fun end(attacker: Profile, victim: Profile) {
        stat(attacker) { it.end(true) }
        stat(victim) { it.end(false) }

        deathAnimation(attacker, victim)

        stat(
            attacker
        ) {
            this.setInventoryStats(
                it
            )
        }
        stat(
            victim
        ) {
            this.setInventoryStats(
                it
            )
        }

        val winMessage = getWinMessage(attacker)
        val loseMessage = getLoseMessage(victim)

        for (profile in participants) {
            profile.player?.sendMessage(CC.SEPARATOR)
            profile.player?.sendMessage(Strings.MATCH_RESULTS)
            profile.player?.spigot()?.sendMessage(winMessage, TextComponents.SPLITTER, loseMessage)
            profile.player?.sendMessage(CC.SEPARATOR)
        }

        resetPearlCooldown(attacker, victim)
        remove(this)

        victim.player?.heal()
        victim.player?.removePotionEffects()
        victim.contest = null

        sendBackToLobby(victim)

        attacker.scoreboard = MatchEndScoreboard.INSTANCE
        victim.scoreboard = DefaultScoreboard.INSTANCE

        Bukkit.getServer().scheduler.runTaskLater(PracticePlugin.INSTANCE, {
            if (attacker.match?.ended == false) return@runTaskLater
            attacker.scoreboard = DefaultScoreboard.INSTANCE
            event.removeMatch(this@EventMatch)

            val eventArena = event.arena

            if (!event.ended) {
                eventArena.waitingLocation.bukkit(this.world)?.let { PlayerUtil.teleport(attacker, it) }
                attacker.playerStatus = PlayerStatus.IDLE
                attacker.inventory.setInventoryForEvent()
            } else {
                attacker.teleportToLobby()
                attacker.inventory.setInventoryForLobby()
            }
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

    override fun sendBackToLobby(profile: Profile) {
        if (event.ended) return super.sendBackToLobby(profile)
        if (profile.match != this) return
        profile.match = null
        profile.spectate(event)
    }
}
