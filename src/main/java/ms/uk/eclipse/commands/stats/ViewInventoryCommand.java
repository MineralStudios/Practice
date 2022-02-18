package ms.uk.eclipse.commands.stats;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.inventory.menus.InventoryStatsMenu;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ErrorMessages;
import ms.uk.eclipse.util.messages.UsageMessages;

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
