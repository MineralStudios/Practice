package gg.mineral.practice.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class EntryListener implements Listener {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final PartyManager partyManager = PracticePlugin.INSTANCE.getPartyManager();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Profile player = playerManager.getProfile(event.getPlayer());
		player.bukkit().setGameMode(GameMode.SURVIVAL);
		player.heal();
		player.setInventoryForLobby();
		player.removePotionEffects();

		new Scoreboard(player).setBoard();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);

		Profile victim = playerManager.getProfile(e.getPlayer());

		victim.removeScoreboard();

		if (victim.isInParty()) {
			Party p = victim.getParty();

			if (p == null) {
				return;
			}

			ChatMessage leftMessage = ChatMessages.LEFT_PARTY.clone().replace("%player%", victim.getName());

			if (p.getPartyLeader().equals(victim)) {
				while (!p.getPartyMembers().isEmpty()) {
					Profile plr = p.getPartyMembers().removeFirst();
					plr.removeFromParty();
					plr.message(leftMessage);
				}

				partyManager.remove(p);
			} else {

				victim.removeFromParty();

				for (Profile plr : p.getPartyMembers()) {
					plr.message(leftMessage);
				}
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

		playerManager.remove(victim);
	}

	@EventHandler
	public void onPlayerInitialSpawn(PlayerInitialSpawnEvent e) {
		e.setSpawnLocation(playerManager.getSpawnLocation());
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		playerManager.getProfile(e.getPlayer());
	}
}
