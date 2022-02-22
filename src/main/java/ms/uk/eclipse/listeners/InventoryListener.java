package ms.uk.eclipse.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.tasks.MenuTask;

public class InventoryListener implements Listener {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Profile player = playerManager.getProfile((org.bukkit.entity.Player) e.getWhoClicked());
		PracticeMenu menu = player.getOpenMenu();

		boolean canClick = player.bukkit().isOp() && player.bukkit().getGameMode().equals(GameMode.CREATIVE);

		e.setCancelled(e.getCurrentItem() == null ? false : e.getCurrentItem().getType() == Material.TNT);

		if (player.getInventoryClickCancelled()) {
			e.setCancelled(!canClick);
		}

		if (menu == null) {
			return;
		}

		if (e.getSlot() < e.getView().getTopInventory().getSize()) {
			e.setCancelled(menu.getClickCancelled());
		}

		Object object = menu.getTask(e.getSlot());

		if (object == null) {
			return;
		}

		if (object instanceof CommandTask) {
			player.bukkit().performCommand(((CommandTask) object).getCommand());
			return;
		}

		if (object instanceof MenuTask) {
			player.openMenu(((MenuTask) object).getMenu());
			return;
		}

		if (object instanceof Runnable) {
			((Runnable) object).run();
			return;
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Profile player = playerManager.getProfile((org.bukkit.entity.Player) e.getPlayer());
		player.setOpenMenu(null);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Profile player = playerManager.getProfile(e.getPlayer());
		boolean canDrop = player.bukkit().isOp() && player.bukkit().getGameMode().equals(GameMode.CREATIVE);

		if (player.getPlayerStatus() == PlayerStatus.KIT_EDITOR) {
			return;
		}

		if (player.getPlayerStatus() == PlayerStatus.FIGHTING) {
			return;
		}

		e.setCancelled(!canDrop);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		Profile player = playerManager.getProfileFromMatch(e.getPlayer());
		e.setCancelled(player == null);
	}
}
