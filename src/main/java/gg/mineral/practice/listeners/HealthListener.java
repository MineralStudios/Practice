package gg.mineral.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;

public class HealthListener implements Listener {

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		if (!(e.getEntity() instanceof org.bukkit.entity.Player)) {
			return;
		}

		Player bukkit = (org.bukkit.entity.Player) e.getEntity();
		Profile profile = PlayerManager
				.get(p -> p.getUUID().equals(bukkit.getUniqueId()) && p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (profile == null) {
			return;
		}

		if (!profile.getMatch().getData().getRegeneration()) {
			if (e.getRegainReason() == RegainReason.SATIATED || e.getRegainReason() == RegainReason.REGEN) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		Player bukkit = (org.bukkit.entity.Player) e.getEntity();
		Profile profile = PlayerManager
				.get(p -> p.getUUID().equals(bukkit.getUniqueId()) && p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (profile == null) {
			e.setCancelled(true);
			return;
		}

		if (profile.isInMatchCountdown()) {
			e.setCancelled(true);
			return;
		}

		e.setCancelled(!profile.getMatch().getData().getHunger());
	}
}
