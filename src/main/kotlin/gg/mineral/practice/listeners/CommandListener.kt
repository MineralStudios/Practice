package gg.mineral.practice.listeners

import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.TabCompleteEvent
import java.util.*

class CommandListener : Listener {
    private val blacklistedCommands: List<String> = listOf(
        "?",
        "pl",
        "me",
        "plugins",
        "bukkit:?",
        "bukkit:pl",
        "bukkit:plugins",
        "minecraft:pl",
        "minecraft:plugins",
        "minecraft:me",
        "buycraftx:buycraft",
        "buycraftx:tebex",
        "buycraft",
        "tebex",
        "knockbacksync",
        "knockbacksync:kbs",
        "kbs",
        "knockbacksync:kbsync",
        "kbsync",
        "knockbacksync:knockbacksync",
        "mineralbot",
        "mineralbot:mineralbot",
        "velocity",
        "velocity:velocity",
        "velocity:callback",
        "callback",
        "luckpermsvelocity",
        "luckpermsvelocity:lpv",
        "lpv",
        "lpx:lpx",
        "lpx",
        "tell",
        "minecraft:tell",
        "bukkit:?",
        "bukkit:pl",
        "bukkit:plugins",
        "bukkit:help",
        "bukkit:about",
        "bukkit:version",
        "bukkit:ver",
        "bukkit:rl",
        "bukkit:reload",
        "bottesting",
        "practice:bottesting",
        "practice:arena",
        "arena",
        "practice:category",
        "category",
        "practice:gametype",
        "gametype",
        "practice:kiteditor",
        "kiteditor",
        "practice:lbconfig",
        "lbconfig",
        "practice:leaderboardconfig",
        "leaderboardconfig",
        "practice:lobby",
        "lobby",
        "practice:parties",
        "parties",
        "practice:practice",
        "practice",
        "practice:queuetype",
        "queuetype",
        "practice:settingsconfig",
        "settingsconfig",
        "practice:specconfig",
        "specconfig",
        "practice:spectateconfig",
        "spectateconfig",
        "practiceconfig",
        "practicecommandslist",
        "version",
        "ver",
        "ultimatetnt:ultimatetnt",
        "ultimatetnt:utnt",
        "utnt",
        "ultimatetnt",
        "about"
    )


    @EventHandler
    fun onCommandUse(event: PlayerCommandPreprocessEvent) {
        if (event.player.hasPermission("practice.command.bypass")) return

        val arrCommand = event.message
            .lowercase(Locale.getDefault())
            .split(" ".toRegex(), limit = 2)
            .toTypedArray()

        for (blacklisted in blacklistedCommands) {
            if (arrCommand[0].equals("/" + blacklisted.lowercase(Locale.getDefault()), ignoreCase = true)) {
                event.isCancelled = true
                break
            }
        }
    }

    @EventHandler
    fun onTabComplete(event: TabCompleteEvent) {
        // Only filter completions for players.
        if (event.sender !is Player) return
        val player = event.sender as Player

        // Get the current buffer (what the player has typed so far)
        val buffer = event.buffer ?: return
        // Split the buffer by whitespace – the first token should be the command.
        val tokens = buffer.trim().split("\\s+".toRegex())
        if (tokens.isEmpty()) return

        // Determine the command label (remove any leading '/' and convert to lowercase)
        val commandLabel = tokens[0].removePrefix("/").lowercase(Locale.getDefault())

        // *** NEW: Block tab completions (including arguments) for blacklisted commands ***
        if (!player.hasPermission("practice.command.bypass") && blacklistedCommands.contains(commandLabel)) {
            event.completions = emptyList()
            return
        }

        val server = Bukkit.getServer()
        // Try to get the PluginCommand object for the command label.
        val pluginCommand: PluginCommand? = server.getPluginCommand(commandLabel)
        if (pluginCommand != null) {
            // If the command has an associated permission and the player doesn’t have it…
            val permission = pluginCommand.permission
            if (!permission.isNullOrBlank() && !player.hasPermission(permission)) {
                // … then clear all tab completions.
                event.completions = emptyList()
                return
            }
        }

        // If the player is tab-completing the first token (i.e. the command name),
        // filter out any suggestions corresponding to commands the player isn’t allowed to use
        // and exclude any blacklisted commands.
        if (tokens.size == 1) {
            val filtered = event.completions.filter { suggestion ->
                // Remove any leading '/' and normalize the suggestion.
                val label = suggestion.removePrefix("/").lowercase(Locale.getDefault())
                // Exclude if this is a blacklisted command.
                if (!player.hasPermission("practice.command.bypass") && blacklistedCommands.contains(label)) {
                    false
                } else {
                    // Check if the command has a permission and if the player is allowed.
                    val cmd = server.getPluginCommand(label)
                    if (cmd != null) {
                        val perm = cmd.permission
                        perm.isNullOrBlank() || player.hasPermission(perm)
                    } else {
                        true // keep suggestions we can’t check
                    }
                }
            }
            event.completions = filtered
        }
    }
}
