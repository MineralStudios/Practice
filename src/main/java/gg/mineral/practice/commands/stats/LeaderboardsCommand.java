package gg.mineral.practice.commands.stats;

import gg.mineral.core.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.inventory.menus.LeaderboardMenu;
import gg.mineral.practice.managers.PlayerManager;

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
