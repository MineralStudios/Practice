package gg.mineral.practice.listeners

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.appender.PlayerAppender
import gg.mineral.practice.managers.EloManager.updateName
import gg.mineral.practice.managers.ProfileManager.getOrCreateProfile
import gg.mineral.practice.managers.ProfileManager.lobbyLocation
import gg.mineral.practice.managers.ProfileManager.remove
import gg.mineral.practice.managers.ProfileManager.removeIfExists
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInitialSpawnEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class EntryListener : Listener, PlayerAppender {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = null
        removeIfExists(event.player)
        event.player.gameMode = GameMode.SURVIVAL
        event.player.heal()

        if (event.player.isFake()) return

        event.player.removePotionEffects()
        updateName(event.player)

        val profile = getOrCreateProfile(event.player)
        profile.inventory.setInventoryForLobby()

        profile.playerStatus = PlayerStatus.IDLE
        profile.scoreboard = DefaultScoreboard.INSTANCE
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        e.quitMessage = null

        val victim = getOrCreateProfile(e.player)

        victim.taskIds.forEach { Bukkit.getScheduler().cancelTask(it) }

        victim.party?.leave(victim)
        victim.contest?.removePlayer(victim)

        when (victim.playerStatus) {
            PlayerStatus.FIGHTING -> victim.match?.end(victim)
            PlayerStatus.QUEUEING -> victim.removeFromQueue()
            else -> {}
        }

        remove(victim)
    }

    @EventHandler
    fun onPlayerInitialSpawn(e: PlayerInitialSpawnEvent) {
        if (e.player.isFake()) return
        e.spawnLocation = lobbyLocation
    }
}
