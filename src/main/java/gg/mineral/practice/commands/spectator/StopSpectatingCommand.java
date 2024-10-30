package gg.mineral.practice.commands.spectator;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.managers.ProfileManager;
import lombok.val;

public class StopSpectatingCommand extends PlayerCommand {

    public StopSpectatingCommand() {
        super("stopspectating");
        setAliases("spec");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        val profile = ProfileManager.getOrCreateProfile(pl);
        profile.getSpectateHandler().stopSpectating();
    }
}
