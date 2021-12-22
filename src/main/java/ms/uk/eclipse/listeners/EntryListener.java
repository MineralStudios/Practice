package ms.uk.eclipse.listeners;

import java.util.Iterator;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.message.LeftMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.party.Party;
import ms.uk.eclipse.scoreboard.Scoreboard;

public class EntryListener implements Listener {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final PartyManager partyManager = PracticePlugin.INSTANCE.getPartyManager();

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		event.getPlayer().setWalkSpeed(0.2F);
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

			LeftMessage m = new LeftMessage(victim.getName(), "party");

			Iterator<Profile> iter = p.getPartyMembers().iterator();

			if (p.getPartyLeader().equals(victim)) {
				while (iter.hasNext()) {
					Profile plr = iter.next();
					iter.remove();
					plr.removeFromParty();
					plr.message(m);
				}

				partyManager.remove(p);
			} else {

				victim.removeFromParty();

				while (iter.hasNext()) {
					Profile plr = iter.next();
					plr.message(m);
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
