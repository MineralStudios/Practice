package gg.mineral.practice.listeners;

import java.util.function.Predicate;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.PlayerManager;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Profile player = PlayerManager.getProfile((org.bukkit.entity.Player) e.getWhoClicked());
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

		Predicate<Profile> predicate = menu.getTask(e.getSlot());

		if (predicate == null) {
			return;
		}

		predicate.test(player);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Profile player = PlayerManager.getProfile((org.bukkit.entity.Player) e.getPlayer());
		player.setOpenMenu(null);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Profile player = PlayerManager.getProfile(e.getPlayer());
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
		Profile player = PlayerManager.getProfileFromMatch(e.getPlayer());
		e.setCancelled(player == null);
	}
}
