package ms.uk.eclipse.commands.stats;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.inventory.menus.InventoryStatsMenu;
import ms.uk.eclipse.managers.PlayerManager;

public class ViewInventoryCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public ViewInventoryCommand() {
		super("viewinventory");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);

		if (args.length < 1) {
			player.message(new UsageMessage("/viewinventory <Player>"));
			return;
		}

		InventoryStatsMenu inventoryStats = playerManager.getInventoryStats(args[0]);

		if (inventoryStats == null) {
			player.message(new ErrorMessage("Inventory was not found"));
			return;
		}

		player.openMenu(new InventoryStatsMenu(inventoryStats));
	}
}
