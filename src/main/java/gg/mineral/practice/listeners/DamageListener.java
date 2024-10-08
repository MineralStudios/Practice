package gg.mineral.practice.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import gg.mineral.practice.bukkit.events.PlayerDamageByArrowEvent;
import gg.mineral.practice.bukkit.events.PlayerDamageByPlayerEvent;
import gg.mineral.practice.bukkit.events.PlayerDamageEvent;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class DamageListener implements Listener {

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {
			e.setCancelled(true);
			return;
		}

		PlayerDamageEvent event;

		if (e instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) e;

			if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
				event = new PlayerDamageByArrowEvent(
						(Arrow) entityDamageByEntityEvent.getDamager(), e);
			} else if (entityDamageByEntityEvent.getDamager() instanceof Player) {
				event = new PlayerDamageByPlayerEvent(
						(Player) entityDamageByEntityEvent.getDamager(), e);
			} else {
				event = new PlayerDamageEvent(e);
			}
		} else {
			event = new PlayerDamageEvent(e);
		}

		Bukkit.getPluginManager().callEvent(event);

		e.setCancelled(event.isCancelled());
	}

	@EventHandler
	public void onPlayerDamage(PlayerDamageEvent e) {
		Player player = e.getPlayer();

		Profile victim = ProfileManager
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

		if (e instanceof PlayerDamageByPlayerEvent || e instanceof PlayerDamageByArrowEvent) {
			return;
		}

		victim.setKiller(null);
	}

	@EventHandler
	public void onPlayerDamageByPlayer(PlayerDamageByPlayerEvent e) {
		Profile attacker = ProfileManager
				.getProfile(e.getDamager().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (attacker == null) {
			e.setCancelled(true);
			return;
		}

		if (attacker.getMatch().isEnded()) {
			e.setCancelled(true);
			return;
		}

		Profile victim = ProfileManager
				.getProfile(e.getPlayer().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		if (victim.getMatch().isEnded()) {
			e.setCancelled(true);
			return;
		}

		if (attacker.getMatch() instanceof TeamMatch
				&& attacker.getMatch().getTeam(attacker).contains(victim)) {
			e.setCancelled(true);
			return;
		}

		if (victim.getPlayer().getNoDamageTicks() <= victim.getPlayer().getMaximumNoDamageTicks() / 2.0f
				&& attacker.getMatch().incrementTeamHitCount(attacker, victim)) {
			e.setCancelled(true);
			return;
		}

		victim.setKiller(attacker);
	}

	@EventHandler
	public void onPlayerDamageByArrow(PlayerDamageByArrowEvent e) {
		Arrow arrow = e.getDamager();

		if (!(arrow.getShooter() instanceof Player))
			return;

		Player shooter = (Player) arrow.getShooter();
		Profile attacker = ProfileManager
				.getProfile(shooter.getUniqueId(), p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (attacker == null) {
			e.setCancelled(true);
			return;
		}

		if (attacker.getMatch().isEnded()) {
			e.setCancelled(true);
			return;
		}

		Profile victim = ProfileManager
				.getProfile(e.getPlayer().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		if (attacker.getMatch() instanceof TeamMatch
				&& attacker.getMatch().getTeam(attacker).contains(victim)) {
			e.setCancelled(true);
			return;
		}

		int health = (int) e.getPlayer().getHealth();
		ChatMessages.HEALTH.clone().replace("%player%", e.getPlayer().getName()).replace("%health%", "" + health)
				.send(shooter);
		shooter.playNote(shooter.getLocation(), Instrument.PIANO, new Note(20));

		victim.setKiller(attacker);
	}

	@EventHandler
	public void onEntityCombustByEntity(EntityCombustByEntityEvent e) {
		Profile attacker = ProfileManager
				.getProfile(e.getCombuster().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (attacker == null) {
			e.setCancelled(true);
			return;
		}

		Profile victim = ProfileManager
				.getProfile(e.getEntity().getUniqueId(),
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (victim == null) {
			e.setCancelled(true);
			return;
		}

		if (attacker.isInParty())
			e.setCancelled(attacker.getMatch().getTeam(attacker).contains(victim));
	}
}
