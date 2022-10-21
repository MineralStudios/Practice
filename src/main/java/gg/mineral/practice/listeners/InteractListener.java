package gg.mineral.practice.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.AddItemsMenu;
import gg.mineral.practice.inventory.menus.SaveLoadKitsMenu;
import gg.mineral.practice.managers.PlayerManager;

import gg.mineral.practice.util.messages.impl.ChatMessages;

public class InteractListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {

		Profile profile = PlayerManager.get(p -> p.getUUID().equals(e.getPlayer().getUniqueId()));

		if (profile.isInMatchCountdown()) {
			e.setCancelled(true);
			return;
		}

		Action eAction = e.getAction();

		if (eAction == Action.PHYSICAL) {
			Block soilBlock = e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
			if (soilBlock.getType() == Material.SOIL) {
				e.setCancelled(true);
				return;
			}
		}

		if (eAction != Action.RIGHT_CLICK_AIR && eAction != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (profile.getPlayerStatus() == PlayerStatus.KIT_CREATOR
				|| profile.getPlayerStatus() == PlayerStatus.KIT_EDITOR) {
			e.setCancelled(true);

			if (e.getClickedBlock() == null) {
				return;
			}

			if (e.getClickedBlock().getType() == Material.ANVIL) {
				profile.openMenu(new SaveLoadKitsMenu());
				return;
			}

			if (profile.getPlayerStatus() == PlayerStatus.KIT_CREATOR) {

				if (e.getClickedBlock().getType() == Material.WOODEN_DOOR) {
					profile.leaveKitCreator();
					return;
				}

				return;
			}

			if (e.getClickedBlock().getType() == Material.CHEST) {
				profile.openMenu(new AddItemsMenu());
				return;
			}

			if (e.getClickedBlock().getType() == Material.WOODEN_DOOR) {
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
							.send(profile.bukkit());
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
						if (profile.bukkit().getHealth() > 20) {
							return;
						}

						profile.getInventory().setItemInHand(new ItemStack(Material.BOWL));

						if (profile.bukkit().getHealth() <= 14.0) {
							profile.bukkit().setHealth(profile.bukkit().getHealth() + 6.0);
							return;
						}

						profile.bukkit().setHealth(20);
					}
				}.runTaskLater(PracticePlugin.INSTANCE, 1);
				return;
			}
		}

		if (e.getClickedBlock() != null) {
			if (e.getClickedBlock().getType() == Material.TNT) {
				Material type = profile.getItemInHand().getType();
				if (type != Material.FLINT_AND_STEEL || type != Material.FIREBALL) {
					return;
				}

				if (profile.getPlayerStatus() != PlayerStatus.FIGHTING) {
					e.setCancelled(true);
					return;
				}

				if (profile.getMatch().getData().getGriefing()) {
					return;
				}

				e.setCancelled(true);
				return;
			}
		}

		Object object = profile.getInventory().getInteractionPredicate(profile.getInventory().getHeldItemSlot());

		if (object == null) {
			return;
		}

		if (object instanceof CommandTask) {
			profile.bukkit().performCommand(((CommandTask) object).getCommand());
			return;
		}

		if (object instanceof MenuTask) {
			profile.openMenu(((MenuTask) object).getMenu());
			return;
		}

		if (object instanceof Runnable) {
			((Runnable) object).run();
			return;
		}

		return;
	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent e) {

		if (e.getBlock().getType() == Material.SOIL) {
			e.setCancelled(true);
		}
	}
}
