package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.EloMenu;
import gg.mineral.practice.managers.PlayerManager;

public class EloCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public EloCommand() {
		super("elo");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);

		if (args.length == 0) {
			player.openMenu(new EloMenu(player));
			return;
		}

		Profile playerarg = playerManager.getProfile(args[0]);

		if (playerarg == null) {
			player.openMenu(new EloMenu(args[0]));
			return;
		}

		player.openMenu(new EloMenu(playerarg));

	}
}
