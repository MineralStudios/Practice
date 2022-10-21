package gg.mineral.practice.listeners;

import java.sql.SQLException;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.scoreboard.Scoreboard;

public class EntryListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Profile player = PlayerManager.getOrCreate(event.getPlayer());
		player.bukkit().setGameMode(GameMode.SURVIVAL);
		player.heal();
		player.setInventoryForLobby();
		player.removePotionEffects();

		new Scoreboard(player).setBoard();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) throws SQLException {
		e.setQuitMessage(null);

		Profile victim = PlayerManager.get(p -> p.getUUID().equals(e.getPlayer().getUniqueId()));

		victim.removeScoreboard();

		if (victim.isInParty()) {
			Party p = victim.getParty();

			if (p.getPartyLeader().equals(victim)) {
				p.disband();
			} else {
				p.leave(victim);
			}
		}

		switch (victim.getPlayerStatus()) {
			case FIGHTING:
				victim.getMatch().end(victim);
				break;
			case IN_TOURAMENT:
				victim.getTournament().removePlayer(victim);
				break;
			case IN_QUEUE:
				victim.removeFromQueue();
				break;
			default:
		}

		PlayerManager.remove(victim);
	}

	@EventHandler
	public void onPlayerInitialSpawn(PlayerInitialSpawnEvent e) {
		e.setSpawnLocation(PlayerManager.getSpawnLocation());
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		PlayerManager.getOrCreate(e.getPlayer());
	}
}
