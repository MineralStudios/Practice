package gg.mineral.practice.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class EntryListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Profile player = ProfileManager.getOrCreateProfile(event.getPlayer());
		player.getPlayer().setGameMode(GameMode.SURVIVAL);
		player.heal();
		player.setInventoryForLobby();
		player.removePotionEffects();

		new DefaultScoreboard(player).setBoard();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);

		Profile victim = ProfileManager.getOrCreateProfile(e.getPlayer());

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

				PartyManager.remove(p);
			} else {

				victim.removeFromParty();

				for (Profile plr : p.getPartyMembers()) {
					plr.message(leftMessage);
				}
			}
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
		e.setSpawnLocation(ProfileManager.getSpawnLocation());
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		ProfileManager.getOrCreateProfile(e.getPlayer());
	}
}
