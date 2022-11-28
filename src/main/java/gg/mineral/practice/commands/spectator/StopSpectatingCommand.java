package gg.mineral.practice.commands.spectator;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;

public class StopSpectatingCommand extends PlayerCommand {
    final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

    public StopSpectatingCommand() {
        super("stopspectating");
        setAliases("spec");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        Profile player = playerManager.getProfile(pl);
        player.stopSpectatingAndFollowing();
    }
}
