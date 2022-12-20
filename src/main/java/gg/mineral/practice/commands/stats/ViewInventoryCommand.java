package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class ViewInventoryCommand extends PlayerCommand {

	public ViewInventoryCommand() {
		super("viewinventory");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile profile = ProfileManager.getOrCreateProfile(pl);

		if (args.length < 1) {
			profile.message(UsageMessages.VIEW_INV);
			return;
		}

		InventoryStatsMenu inventoryStats = ProfileManager.getInventoryStats(args[0]);

		if (inventoryStats == null) {
			profile.message(ErrorMessages.PLAYER_INVENTORY_NOT_FOUND);
			return;
		}

		profile.openMenu(new InventoryStatsMenu(inventoryStats));
	}
}
