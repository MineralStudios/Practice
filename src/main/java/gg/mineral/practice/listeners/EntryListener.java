package gg.mineral.practice.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.EloManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;

public class EntryListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		ProfileManager.removeIfExists(event.getPlayer());
		Profile profile = ProfileManager.getOrCreateProfile(event.getPlayer());
		profile.getPlayer().setGameMode(GameMode.SURVIVAL);
		profile.heal();

		if (BotAPI.INSTANCE.isFakePlayer(profile.getPlayer().getUniqueId()))
			return;

		EloManager.updateName(profile);
		profile.getInventory().setInventoryForLobby();
		profile.removePotionEffects();
		profile.updateVisiblity();

		profile.setScoreboard(DefaultScoreboard.INSTANCE);

		if (profile.getPlayer().hasPermission("practice.fly")) {
			profile.getPlayer().setAllowFlight(true);
		} else if (profile.getPlayer().getAllowFlight()) {
			profile.getPlayer().setAllowFlight(false);
			profile.getPlayer().setFlying(false);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);

		Profile victim = ProfileManager.getOrCreateProfile(e.getPlayer());

		victim.removeScoreboard();

		if (victim.isInParty()) {
			victim.getParty().leave(victim);
		} else if (victim.isInTournament()) {
			victim.getTournament().removePlayer(victim);
		} else if (victim.isInEvent()) {
			victim.getEvent().removePlayer(victim);
		}

		switch (victim.getPlayerStatus()) {
			case FIGHTING:
				victim.getMatch().end(victim);
				break;
			case QUEUEING:
				victim.removeFromQueue();
				break;
			default:
		}

		ProfileManager.remove(victim);
	}

	@EventHandler
	public void onPlayerInitialSpawn(PlayerInitialSpawnEvent e) {

		if (BotAPI.INSTANCE.isFakePlayer(e.getPlayer().getUniqueId()))
			return;

		e.setSpawnLocation(ProfileManager.getSpawnLocation());
	}
}
