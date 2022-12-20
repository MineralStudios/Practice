package gg.mineral.practice.commands.spectator;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;

public class StopSpectatingCommand extends PlayerCommand {

    public StopSpectatingCommand() {
        super("stopspectating");
        setAliases("spec");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        Profile profile = ProfileManager.getOrCreateProfile(pl);
        profile.stopSpectatingAndFollowing();
    }
}
