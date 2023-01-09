package gg.mineral.practice.listeners;

import java.util.function.Predicate;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.AddItemsMenu;
import gg.mineral.practice.inventory.menus.SaveLoadKitsMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class InteractListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {

		Profile profile = ProfileManager.getOrCreateProfile(e.getPlayer());

		Action action = e.getAction();

		if (profile.isInMatchCountdown() || (action == Action.PHYSICAL
				&& e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SOIL)) {
			e.setCancelled(true);
			return;
		}

		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			profile.getMatchStatisticCollector().click();
		}

		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		Predicate<Profile> predicate = profile.getInventory().getTask(profile.getInventory().getHeldItemSlot());

		if (predicate != null && predicate.test(profile)) {
			return;
		}

		if (profile.isInKitCreator()
				|| profile.isInKitEditor()) {
			e.setCancelled(true);

			if (e.getClickedBlock() == null) {
				return;
			}

			if (e.getClickedBlock().getType() == Material.ANVIL) {
				profile.openMenu(new SaveLoadKitsMenu());
				return;
			}

			if (e.getClickedBlock().getType() == Material.CHEST) {
				profile.openMenu(new AddItemsMenu());
				return;
			}

			if (e.getClickedBlock().getType() == Material.WOODEN_DOOR) {
				if (profile.isInKitCreator()) {
					profile.leaveKitCreator();
					return;
				}

				profile.leaveKitEditor();
				return;
			}

			return;
		}

		if (e.getMaterial() != null) {
			if (e.getMaterial() == Material.ENDER_PEARL) {

				if (profile.getPlayerStatus() != PlayerStatus.FIGHTING) {
					return;
				}

				if (profile.isInMatchCountdown()) {
					e.setCancelled(true);
					return;
				}

				if (profile.getPearlCooldown().isActive()) {
					e.setCancelled(true);
					ChatMessages.PEARL.clone().replace("%time%", "" + profile.getPearlCooldown().getTimeRemaining())
							.send(profile.getPlayer());
					return;
				}

				profile.getPearlCooldown().setTimeRemaining(profile.getMatch().getData().getPearlCooldown());
				e.setCancelled(false);

				return;
			}

			if (e.getMaterial() == Material.MUSHROOM_SOUP) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (profile.getPlayer().getHealth() >= 20) {
							return;
						}

						profile.getInventory().setItemInHand(ItemStacks.EMPTY_BOWL);

						if (profile.getPlayer().getHealth() <= 14.0) {
							profile.getPlayer().setHealth(profile.getPlayer().getHealth() + 6.0);
							return;
						}

						profile.getPlayer().setHealth(20);
					}
				}.runTaskLater(PracticePlugin.INSTANCE, 1);
				return;
			}
		}

		if (e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.TNT) {
			Material type = profile.getInventory().getItemInHand().getType();
			if (type != Material.FLINT_AND_STEEL || type != Material.FIREBALL) {
				return;
			}

			if (profile.getPlayerStatus() != PlayerStatus.FIGHTING) {
				e.setCancelled(true);
				return;
			}

			e.setCancelled(!profile.getMatch().getData().getGriefing());
			return;
		}

	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent e) {
		e.setCancelled(e.getBlock().getType() == Material.SOIL);
	}
}
