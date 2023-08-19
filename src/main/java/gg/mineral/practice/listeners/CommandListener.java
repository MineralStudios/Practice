package gg.mineral.practice.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("practice.command.bypass"))
            return;

        List<String> commands = Arrays.asList("?", "pl", "me", "plugins", "bukkit:?", "bukkit:pl", "bukkit:plugins",
                "minecraft:pl", "minecraft:plugins", "minecraft:me");
        commands.forEach(all -> {
            String[] arrCommand = event.getMessage().toLowerCase().split(" ", 2);
            if (arrCommand[0].equalsIgnoreCase("/" + all.toLowerCase())) {
                event.setCancelled(true);
            }
        });
    }
}
