package ms.uk.eclipse.commands.spectator;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;

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
