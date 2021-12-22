package ms.uk.eclipse.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;

public class HealthListener implements Listener {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent e) {
		if (!(e.getEntity() instanceof org.bukkit.entity.Player)) {
			return;
		}

		Profile player = playerManager.getProfileFromMatch((org.bukkit.entity.Player) e.getEntity());

		if (player == null) {
			return;
		}

		if (!player.getMatch().getData().getRegeneration()) {
			if (e.getRegainReason() == RegainReason.SATIATED || e.getRegainReason() == RegainReason.REGEN) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		Profile player = playerManager.getProfileFromMatch((org.bukkit.entity.Player) e.getEntity());

		if (player == null) {
			e.setCancelled(true);
			return;
		}

		if (player.isInMatchCountdown()) {
			e.setCancelled(true);
			return;
		}

		e.setCancelled(!player.getMatch().getData().getHunger());
	}
}
