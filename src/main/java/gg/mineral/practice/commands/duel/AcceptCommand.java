package gg.mineral.practice.commands.duel;

import java.util.Iterator;
import java.util.Map.Entry;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.match.DuelRequest;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.MatchData;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class AcceptCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public AcceptCommand() {
		super("accept");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);

		if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (args.length == 0) {
			player.message(UsageMessages.ACCEPT);
			return;
		}

		Profile player1 = playerManager.getProfile(args[0]);

		if (player1 == null) {
			player.message(ErrorMessages.DUEL_SENDER_NOT_ONLINE);
			return;
		}

		if (player1.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			player.message(ErrorMessages.DUEL_SENDER_NOT_IN_LOBBY);
			return;
		}

		Iterator<Entry<DuelRequest, Long>> it = player.getRecievedDuelRequests().entryIterator();

		while (it.hasNext()) {
			DuelRequest duelRequest = it.next().getKey();

			if (!duelRequest.getSender().equals(player1)) {
				continue;
			}

			it.remove();
			MatchData m = duelRequest.getMatchData();
			Match match = player1.isInParty() && player.isInParty()
					? new PartyMatch(player1.getParty(), player.getParty(), m)
					: new Match(player1, player, m);
			match.start();
			return;
		}
	}
}
