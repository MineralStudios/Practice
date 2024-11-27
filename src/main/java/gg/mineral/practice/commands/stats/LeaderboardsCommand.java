package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.inventory.menus.LeaderboardMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;

public class LeaderboardsCommand extends PlayerCommand {

	public LeaderboardsCommand() {
		super("leaderboards");
		setAliases("leaderboard", "lb");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		QueuetypeManager.getQueuetypes().values().stream().filter(Queuetype::isRanked).findAny()
				.ifPresent(queuetype -> ProfileManager.getOrCreateProfile(pl).openMenu(new LeaderboardMenu(queuetype)));
	}
}
