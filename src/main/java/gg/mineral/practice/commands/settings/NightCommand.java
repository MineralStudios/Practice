package gg.mineral.practice.commands.settings;

import org.bukkit.entity.Player;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class NightCommand extends PlayerCommand {

    public NightCommand() {
        super("night");
    }

    @Override
    public void execute(Player player, String[] args) {
        ChatMessages.TIME_SET_NIGHT.send(player);
        player.setPlayerTime(14000L, false);
        ProfileManager.getOrCreateProfile(player).setNightMode(true);
    }
}