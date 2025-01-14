package gg.mineral.practice.listeners

import gg.mineral.practice.util.messages.CC
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeathListener : Listener {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        // e.setDropItems(false);
        e.deathMessage = null
        e.entity.kickPlayer(CC.RED + "Player death is not allowed.")
    }
}
