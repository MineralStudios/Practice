package ms.uk.eclipse.listeners;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ChatMessages;

public class DamageListener implements Listener {
	PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof org.bukkit.entity.Player)) {
			e.setCancelled(true);
			return;
		}

		org.bukkit.entity.Player player = (org.bukkit.entity.Player) e.getEntity();

		Profile victim = playerManager.getProfileFromMatch(player);

		if (victim == null) {

			if (e.getCause() == DamageCause.VOID) {
				player.teleport(playerManager.getSpawnLocation());
			}

			e.setCancelled(true);
			return;
		}

		if (victim.isInMatchCountdown()) {
			e.setCancelled(true);
			return;
		}

		if (e.getFinalDamage() >= player.getHealth()) {
			victim.getMatch().end(victim);
			e.setCancelled(true);
			return;
		}

		if (e.getCause() == DamageCause.VOID) {
			victim.getMatch().end(victim);
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (!(e.getEntity() instanceof org.bukkit.entity.Player)) {
			e.setCancelled(true);
			return;
		}

		org.bukkit.entity.Player bukkitVictim = (org.bukkit.entity.Player) e.getEntity();

		if (e.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) e.getDamager();

			if (!(arrow.getShooter() instanceof org.bukkit.entity.Player)) {
				return;
			}

			org.bukkit.entity.Player shooter = (org.bukkit.entity.Player) arrow.getShooter();

			int health = (int) bukkitVictim.getHealth();
			ChatMessages.HEALTH.clone().replace("%player%", bukkitVictim.getName()).replace("%health%", "" + health)
					.send(shooter);
			shooter.playNote(shooter.getLocation(), Instrument.PIANO, new Note(20));
			return;
		}

		if (!(e.getDamager() instanceof org.bukkit.entity.Player)) {
			return;
		}

		Profile attacker = playerManager.getProfileFromMatch((org.bukkit.entity.Player) e.getDamager());

		if (attacker == null) {
			e.setCancelled(true);
			return;
		}

		Profile victim = playerManager.getProfileFromMatch(bukkitVictim);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		attacker.increaseHitCount();

		if (attacker.getHitCount() >= 100) {
			victim.getMatch().end(victim);
			return;
		}

		if (attacker.isInParty() && victim.isInParty()) {
			e.setCancelled(attacker.getMatch().getTeam(attacker).contains(victim));
		}
	}

	@EventHandler
	public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
		if (!(e.getEntity() instanceof org.bukkit.entity.Player)) {
			e.setCancelled(true);
			return;
		}

		if (!(e.getCombuster() instanceof org.bukkit.entity.Player)) {
			e.setCancelled(true);
			return;
		}

		Profile attacker = playerManager.getProfileFromMatch((org.bukkit.entity.Player) e.getCombuster());

		if (attacker == null) {
			e.setCancelled(true);
			return;
		}

		Profile victim = playerManager.getProfileFromMatch((org.bukkit.entity.Player) e.getEntity());

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		if (attacker.isInParty()) {
			e.setCancelled(attacker.getMatch().getTeam(attacker).contains(victim));
		}
	}
}
