package gg.mineral.practice.listeners;

import java.util.Arrays;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import lombok.val;

public class CommandListener implements Listener {
    @EventHandler
    public void onCommandUse(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().hasPermission("practice.command.bypass"))
            return;

        val commands = Arrays.asList("?", "pl", "me", "plugins", "bukkit:?", "bukkit:pl", "bukkit:plugins",
                "minecraft:pl", "minecraft:plugins", "minecraft:me");

        val arrCommand = event.getMessage().toLowerCase().split(" ", 2);

        for (val all : commands)
            if (arrCommand[0].equalsIgnoreCase("/" + all.toLowerCase())) {
                event.setCancelled(true);
                break;
            }
    }
}
