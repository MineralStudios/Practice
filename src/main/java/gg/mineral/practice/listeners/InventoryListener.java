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
import gg.mineral.practice.managers.ProfileManager;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Profile player = ProfileManager.getOrCreateProfile((org.bukkit.entity.Player) e.getWhoClicked());
		PracticeMenu menu = player.getOpenMenu();

		boolean canClick = player.getPlayer().isOp() && player.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		e.setCancelled(e.getCurrentItem() == null ? false : e.getCurrentItem().getType() == Material.TNT);

		if (player.isInventoryClickCancelled()) {
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
		Profile player = ProfileManager.getOrCreateProfile((org.bukkit.entity.Player) e.getPlayer());
		player.setOpenMenu(null);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Profile player = ProfileManager.getOrCreateProfile(e.getPlayer());
		boolean canDrop = player.getPlayer().isOp() && player.getPlayer().getGameMode().equals(GameMode.CREATIVE);

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
		e.setCancelled(ProfileManager
				.getProfile(p -> p.getUUID().equals(e.getPlayer().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING) == null);
	}
}
