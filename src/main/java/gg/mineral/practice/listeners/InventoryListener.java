package gg.mineral.practice.listeners;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.Interaction;
import gg.mineral.practice.inventory.Menu;
import gg.mineral.practice.managers.ProfileManager;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Profile profile = ProfileManager.getOrCreateProfile((org.bukkit.entity.Player) e.getWhoClicked());
		Menu menu = profile.getOpenMenu();

		boolean canClick = profile.getPlayer().isOp() && profile.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		ClickType clickType = e.getClick();

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
					menu.getClickCancelled());

		Consumer<Interaction> predicate = menu.getTask(e.getSlot());

		if (predicate == null) {
			return;
		}

		predicate.accept(new Interaction(profile, clickType));
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Profile profile = ProfileManager.getOrCreateProfile((org.bukkit.entity.Player) e.getPlayer());
		final Menu oldMenu = profile.getOpenMenu();
		profile.setOpenMenu(null);

		if (oldMenu != null && !oldMenu.isClosed() && e.getInventory().equals(oldMenu.getInventory())) {
			oldMenu.setClosed(true);

			Bukkit.getScheduler().scheduleSyncDelayedTask(PracticePlugin.INSTANCE, () -> {
				oldMenu.onClose();
			}, 1);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Profile profile = ProfileManager.getOrCreateProfile(e.getPlayer());
		boolean canDrop = profile.getPlayer().isOp() && profile.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		if (profile.isInKitCreator() || profile.isInKitEditor()) {
			e.setCancelled(false);
			Bukkit.getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> e.getItemDrop().remove(), 20L);
			return;
		}

		if (profile.getPlayerStatus() == PlayerStatus.FIGHTING)

		{
			profile.getMatch().getItemRemovalQueue().add(e.getItemDrop());
			return;
		}

		e.setCancelled(!canDrop);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		boolean canPickup = e.getPlayer().isOp() && e.getPlayer().getGameMode().equals(GameMode.CREATIVE);

		if (canPickup)
			return;

		e.setCancelled(ProfileManager
				.getProfile(p -> p.getUuid().equals(e.getPlayer().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING) == null);
	}
}
