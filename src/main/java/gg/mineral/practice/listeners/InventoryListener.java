package gg.mineral.practice.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;

public class InventoryListener implements Listener {

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Profile profile = PlayerManager.get(p -> p.getUUID().equals(e.getPlayer().getUniqueId()));
		boolean canDrop = profile.bukkit().isOp() && profile.bukkit().getGameMode().equals(GameMode.CREATIVE);

		if (profile.getPlayerStatus() == PlayerStatus.KIT_EDITOR) {
			return;
		}

		if (profile.getPlayerStatus() == PlayerStatus.FIGHTING) {
			return;
		}

		e.setCancelled(!canDrop);
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		Profile profile = PlayerManager
				.get(p -> p.getUUID().equals(e.getPlayer().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);
		e.setCancelled(profile == null);
	}
}
