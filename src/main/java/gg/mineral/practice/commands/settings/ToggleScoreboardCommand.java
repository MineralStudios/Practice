package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.val;

public class ToggleScoreboardCommand extends PlayerCommand {

    public ToggleScoreboardCommand() {
        super("togglescoreboard");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        val profile = ProfileManager.getOrCreateProfile(pl);
        profile.setScoreboardEnabled(!profile.isScoreboardEnabled());
        ChatMessages.SCOREBOARD_TOGGLED.clone()
                .replace("%toggled%", profile.isScoreboardEnabled() ? "enabled" : "disabled")
                .send(pl);
    }
}
