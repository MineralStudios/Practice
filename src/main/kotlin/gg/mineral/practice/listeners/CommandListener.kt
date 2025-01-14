package gg.mineral.practice.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.*

class CommandListener : Listener {
    @EventHandler
    fun onCommandUse(event: PlayerCommandPreprocessEvent) {
        if (event.player.hasPermission("practice.command.bypass")) return

        val commands: List<String> = mutableListOf(
            "?", "pl", "me", "plugins", "bukkit:?", "bukkit:pl", "bukkit:plugins",
            "minecraft:pl", "minecraft:plugins", "minecraft:me"
        )

        val arrCommand = event.message.lowercase(Locale.getDefault()).split(" ".toRegex(), limit = 2).toTypedArray()

        for (all in commands) if (arrCommand[0].equals("/" + all.lowercase(Locale.getDefault()), ignoreCase = true)) {
            event.isCancelled = true
            break
        }
    }
}
