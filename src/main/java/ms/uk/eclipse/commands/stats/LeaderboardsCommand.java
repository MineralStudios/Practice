package ms.uk.eclipse.commands.stats;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.inventory.menus.LeaderboardMenu;
import ms.uk.eclipse.managers.PlayerManager;

public class LeaderboardsCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public LeaderboardsCommand() {
		super("leaderboards");
		setAliases("lb");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		playerManager.getProfile(pl).openMenu(new LeaderboardMenu());
	}
}
