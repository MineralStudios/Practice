package gg.mineral.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.inventory.Interaction;
import gg.mineral.practice.managers.ProfileManager;
import lombok.val;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		val profile = ProfileManager.getOrCreateProfile((org.bukkit.entity.Player) e.getWhoClicked());
		val menu = profile.getOpenMenu();

		boolean canClick = profile.getPlayer().isOp() && profile.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		val clickType = e.getClick();

		e.setCancelled(e.getCurrentItem() == null ? false : e.getCurrentItem().getType() == Material.TNT);

		if (profile.getInventory().isInventoryClickCancelled())
			e.setCancelled(!canClick);

		if (menu == null)
			return;

		if (e.getClickedInventory() == null || !e.getClickedInventory().equals(e.getView().getTopInventory()))
			return;

		if (e.getInventory() == null || !e.getInventory().equals(menu.getInventory()))
			return;

		if (e.getSlot() < e.getView().getTopInventory().getSize())
			e.setCancelled(
					menu.isClickCancelled());

		val predicate = menu.getTask(e.getSlot());

		if (predicate == null)
			return;

		predicate.accept(new Interaction(profile, clickType));
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		val profile = ProfileManager.getOrCreateProfile((org.bukkit.entity.Player) e.getPlayer());
		val oldMenu = profile.getOpenMenu();
		profile.setOpenMenu(null);

		if (oldMenu != null && !oldMenu.isClosed() && e.getInventory().equals(oldMenu.getInventory())) {
			oldMenu.setClosed(true);
			Bukkit.getScheduler().scheduleSyncDelayedTask(PracticePlugin.INSTANCE, () -> oldMenu.onClose(), 1);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		val player = e.getPlayer();
		boolean canDrop = player.isOp() && player.getGameMode().equals(GameMode.CREATIVE);

		val profile = ProfileManager.getProfile(player);

		if (profile != null) {
			if (profile.getPlayerStatus() == PlayerStatus.KIT_CREATOR
					|| profile.getPlayerStatus() == PlayerStatus.KIT_EDITOR) {
				e.setCancelled(false);
				Bukkit.getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> e.getItemDrop().remove(), 20L);
				return;
			}

			val match = profile.getMatch();

			if (match != null) {
				match.getItemRemovalQueue().add(e.getItemDrop());
				return;
			}
		}

		e.setCancelled(!canDrop);

	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		boolean canPickup = e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		if (canPickup)
			return;

		e.setCancelled(ProfileManager
				.getProfile(e.getPlayer().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING) == null);
	}
}
