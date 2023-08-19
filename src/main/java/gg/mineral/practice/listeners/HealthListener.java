package gg.mineral.practice.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;

public class HealthListener implements Listener {

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		Profile profile = ProfileManager
				.getProfile(p -> p.getUUID().equals(e.getEntity().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (profile == null)
			return;

		if (!profile.getMatch().getData().getRegeneration()) {
			if (e.getRegainReason() == RegainReason.SATIATED || e.getRegainReason() == RegainReason.REGEN) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		Profile profile = ProfileManager
				.getProfile(p -> p.getUUID().equals(e.getEntity().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);

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
