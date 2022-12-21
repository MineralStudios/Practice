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
		Profile profile = ProfileManager.getOrCreateProfile((org.bukkit.entity.Player) e.getWhoClicked());
		PracticeMenu menu = profile.getOpenMenu();

		boolean canClick = profile.getPlayer().isOp() && profile.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		e.setCancelled(e.getCurrentItem() == null ? false : e.getCurrentItem().getType() == Material.TNT);

		if (profile.getInventory().isInventoryClickCancelled()) {
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

		predicate.test(profile);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Profile profile = ProfileManager.getOrCreateProfile((org.bukkit.entity.Player) e.getPlayer());
		profile.setOpenMenu(null);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Profile profile = ProfileManager.getOrCreateProfile(e.getPlayer());
		boolean canDrop = profile.getPlayer().isOp() && profile.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		if (profile.isInKitCreator() || profile.isInKitEditor()) {
			return;
		}

		if (profile.getPlayerStatus() == PlayerStatus.FIGHTING) {
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
