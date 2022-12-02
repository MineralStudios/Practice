package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.inventory.menus.LeaderboardMenu;
import gg.mineral.practice.managers.PlayerManager;

public class LeaderboardsCommand extends PlayerCommand {

	public LeaderboardsCommand() {
		super("leaderboards");
		setAliases("lb");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		PlayerManager.getProfile(pl).openMenu(new LeaderboardMenu());
	}
}
