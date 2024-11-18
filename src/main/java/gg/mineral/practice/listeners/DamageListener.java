package gg.mineral.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import gg.mineral.practice.bukkit.events.PlayerDamageByPlayerEvent;
import gg.mineral.practice.bukkit.events.PlayerDamageByProjectileEvent;
import gg.mineral.practice.bukkit.events.PlayerDamageEvent;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.val;

public class DamageListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player))
			return;

		PlayerDamageEvent event;

		if (e instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
			if (entityDamageByEntityEvent.getDamager() instanceof Projectile projectile)
				event = new PlayerDamageByProjectileEvent(
						projectile, e);
			else if (entityDamageByEntityEvent.getDamager() instanceof Player damager)
				event = new PlayerDamageByPlayerEvent(
						damager, e);
			else
				event = new PlayerDamageEvent(e);
		} else
			event = new PlayerDamageEvent(e);

		Bukkit.getPluginManager().callEvent(event);

		e.setCancelled(event.isCancelled());
	}

	@EventHandler
	public void onPlayerDamage(PlayerDamageEvent e) {
		val player = e.getPlayer();

		val victim = ProfileManager
				.getProfile(player.getUniqueId(), p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			if (e.getCause() == DamageCause.VOID)
				player.teleport(ProfileManager.getSpawnLocation());

			e.setCancelled(true);
			return;
		}

		if (victim.isInMatchCountdown() || victim.getMatch().isEnded()) {
			e.setCancelled(true);
			return;
		}

		if (e.getCause() == DamageCause.VOID || e.getFinalDamage() >= player.getHealth()) {
			victim.getMatch().end(victim);
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerDamageByPlayer(PlayerDamageByPlayerEvent e) {
		val attacker = ProfileManager
				.getProfile(e.getDamager().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (attacker == null || attacker.getMatch().isEnded()) {
			e.setCancelled(true);
			return;
		}

		val victim = ProfileManager
				.getProfile(e.getPlayer().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		victim.setKiller(attacker);

		if (victim.getMatch().isEnded()) {
			e.setCancelled(true);
			return;
		}

		if (attacker.getMatch() instanceof TeamMatch
				&& attacker.getMatch().getTeam(attacker, true).contains(victim)) {
			e.setCancelled(true);
			return;
		}

		if (victim.getPlayer().getNoDamageTicks() <= victim.getPlayer().getMaximumNoDamageTicks() / 2.0f
				&& attacker.getMatch().incrementTeamHitCount(attacker, victim)) {
			e.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void onPlayerDamageByProjectile(PlayerDamageByProjectileEvent e) {
		val projectile = e.getDamager();

		if (!(projectile.getShooter() instanceof Player))
			return;

		val shooter = (Player) projectile.getShooter();
		val attacker = ProfileManager
				.getProfile(shooter.getUniqueId(), p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (attacker == null || attacker.getMatch().isEnded()) {
			e.setCancelled(true);
			return;
		}

		val victim = ProfileManager
				.getProfile(e.getPlayer().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		if (attacker.getMatch() instanceof TeamMatch
				&& attacker.getMatch().getTeam(attacker, true).contains(victim)) {
			e.setCancelled(true);
			return;
		}

		if (projectile instanceof Arrow) {
			int health = (int) e.getPlayer().getHealth();
			ChatMessages.HEALTH.clone().replace("%player%", e.getPlayer().getName()).replace("%health%", "" + health)
					.send(shooter);
			shooter.playNote(shooter.getLocation(), Instrument.PIANO, new Note(20));
		}

		victim.setKiller(attacker);
	}

	@EventHandler
	public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
		val attacker = ProfileManager
				.getProfile(e.getCombuster().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (attacker == null) {
			e.setCancelled(true);
			return;
		}

		val victim = ProfileManager
				.getProfile(e.getEntity().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		if (attacker.isInParty())
			e.setCancelled(attacker.getMatch().getTeam(attacker, true).contains(victim));
	}
}
