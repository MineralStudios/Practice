package ms.uk.eclipse.listeners;

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

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.menus.AddItemsMenu;
import ms.uk.eclipse.inventory.menus.SaveLoadKitsMenu;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.tasks.MenuTask;
import ms.uk.eclipse.util.messages.ChatMessages;

public class InteractListener implements Listener {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {

		Profile player = playerManager.getProfile(e.getPlayer());

		if (player.isInMatchCountdown()) {
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

		if (player.getPlayerStatus() == PlayerStatus.KIT_CREATOR
				|| player.getPlayerStatus() == PlayerStatus.KIT_EDITOR) {
			e.setCancelled(true);

			if (e.getClickedBlock() == null) {
				return;
			}

			if (e.getClickedBlock().getType() == Material.ANVIL) {
				player.openMenu(new SaveLoadKitsMenu());
				return;
			}

			if (player.getPlayerStatus() == PlayerStatus.KIT_CREATOR) {

				if (e.getClickedBlock().getType() == Material.WOODEN_DOOR) {
					player.leaveKitCreator();
					return;
				}

				return;
			}

			if (e.getClickedBlock().getType() == Material.CHEST) {
				player.openMenu(new AddItemsMenu());
				return;
			}

			if (e.getClickedBlock().getType() == Material.WOODEN_DOOR) {
				player.leaveKitEditor();
				return;
			}

			return;
		}

		if (e.getMaterial() != null) {
			if (e.getMaterial() == Material.ENDER_PEARL) {

				if (player.getPlayerStatus() != PlayerStatus.FIGHTING) {
					return;
				}

				if (player.isInMatchCountdown()) {
					e.setCancelled(true);
					return;
				}

				if (player.getPearlCooldown().isActive()) {
					e.setCancelled(true);
					ChatMessages.PEARL.clone().replace("%time%", "" + player.getPearlCooldown().getTimeRemaining())
							.send(player.bukkit());
					return;
				}

				player.getPearlCooldown().setTimeRemaining(player.getMatch().getData().getPearlCooldown());
				e.setCancelled(false);

				return;
			}

			if (e.getMaterial() == Material.MUSHROOM_SOUP) {
				new BukkitRunnable() {
					@Override
					public void run() {
						if (player.bukkit().getHealth() > 20) {
							return;
						}

						player.getInventory().setItemInHand(new ItemStack(Material.BOWL));

						if (player.bukkit().getHealth() <= 14.0) {
							player.bukkit().setHealth(player.bukkit().getHealth() + 6.0);
							return;
						}

						player.bukkit().setHealth(20);
					}
				}.runTaskLater(PracticePlugin.INSTANCE, 1);
				return;
			}
		}

		if (e.getClickedBlock() != null) {
			if (e.getClickedBlock().getType() == Material.TNT) {
				Material type = player.getItemInHand().getType();
				if (type != Material.FLINT_AND_STEEL || type != Material.FIREBALL) {
					return;
				}

				if (player.getPlayerStatus() != PlayerStatus.FIGHTING) {
					e.setCancelled(true);
					return;
				}

				if (player.getMatch().getData().getGriefing()) {
					return;
				}

				e.setCancelled(true);
				return;
			}
		}

		Object object = player.getInventory().getTask(player.getInventory().getHeldItemSlot());

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

		return;
	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent e) {

		if (e.getBlock().getType() == Material.SOIL) {
			e.setCancelled(true);
		}
	}
}
