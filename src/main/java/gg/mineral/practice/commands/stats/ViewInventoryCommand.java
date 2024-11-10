package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.inventory.menus.InventoryStatsListMenu;

import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;
import lombok.val;

public class ViewInventoryCommand extends PlayerCommand {

	public ViewInventoryCommand() {
		super("viewinventory");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		val profile = ProfileManager.getOrCreateProfile(pl);

		if (args.length < 1) {
			profile.message(UsageMessages.VIEW_INV);
			return;
		}

		val inventoryStats = ProfileManager.getInventoryStats(args[0]);

		if (inventoryStats == null || inventoryStats.isEmpty()) {
			profile.message(ErrorMessages.PLAYER_INVENTORY_NOT_FOUND);
			return;
		}

		if (inventoryStats.size() == 1) {
			profile.openMenu(inventoryStats.get(0));
			return;
		}

		profile.openMenu(new InventoryStatsListMenu(inventoryStats));
	}
}
