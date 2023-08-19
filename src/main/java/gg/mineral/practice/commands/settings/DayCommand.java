package gg.mineral.practice.commands.settings;

import org.bukkit.entity.Player;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class DayCommand extends PlayerCommand {

    public DayCommand() {
        super("day");
    }

    @Override
    public void execute(Player player, String[] args) {
        ChatMessages.TIME_SET_DAY.send(player);
        player.setPlayerTime(0L, false);
        ProfileManager.getOrCreateProfile(player).setNightMode(false);
    }
}