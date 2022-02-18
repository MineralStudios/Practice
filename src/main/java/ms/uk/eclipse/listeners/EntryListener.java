package ms.uk.eclipse.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.party.Party;
import ms.uk.eclipse.scoreboard.Scoreboard;
import ms.uk.eclipse.util.messages.ChatMessages;

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
