package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class ViewInventoryCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public ViewInventoryCommand() {
		super("viewinventory");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);

		if (args.length < 1) {
			player.message(UsageMessages.VIEW_INV);
			return;
		}

		InventoryStatsMenu inventoryStats = playerManager.getInventoryStats(args[0]);

		if (inventoryStats == null) {
			player.message(ErrorMessages.PLAYER_INVENTORY_NOT_FOUND);
			return;
		}

		player.openMenu(new InventoryStatsMenu(inventoryStats));
	}
}
