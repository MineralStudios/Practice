package gg.mineral.practice.listeners;

import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class DamageListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof org.bukkit.entity.Player)) {
			e.setCancelled(true);
			return;
		}

		org.bukkit.entity.Player player = (org.bukkit.entity.Player) e.getEntity();

		Profile victim = ProfileManager
				.getProfile(
						p -> p.getUUID().equals(player.getUniqueId()) && p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {

			if (e.getCause() == DamageCause.VOID) {
				player.teleport(ProfileManager.getSpawnLocation());
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

		Profile attacker = ProfileManager
				.getProfile(p -> p.getUUID().equals(e.getDamager().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (attacker == null) {
			e.setCancelled(true);
			return;
		}

		Profile victim = ProfileManager
				.getProfile(p -> p.getUUID().equals(bukkitVictim.getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		attacker.increaseHitCount();
		victim.resetCombo();

		if (attacker.getHitCount() >= 100 && attacker.getMatchData().getBoxing()) {
			victim.getMatch().end(victim);
			return;
		}

		if (attacker.isInParty() && victim.isInParty()) {
			e.setCancelled(attacker.getMatch().getTeam(attacker).contains(victim));
		}
	}

	@EventHandler
	public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
		Profile attacker = ProfileManager
				.getProfile(p -> p.getUUID().equals(e.getCombuster().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (attacker == null) {
			e.setCancelled(true);
			return;
		}

		Profile victim = ProfileManager
				.getProfile(p -> p.getUUID().equals(e.getEntity().getUniqueId())
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		if (attacker.isInParty()) {
			e.setCancelled(attacker.getMatch().getTeam(attacker).contains(victim));
		}
	}
}
