package gg.mineral.practice.commands.duel;

import java.util.Iterator;
import java.util.Map.Entry;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.request.DuelRequest;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class AcceptCommand extends PlayerCommand {

	public AcceptCommand() {
		super("accept");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = ProfileManager.getOrCreateProfile(pl);

		if (player.getPlayerStatus() != PlayerStatus.IDLE) {
			player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (args.length == 0) {
			player.message(UsageMessages.ACCEPT);
			return;
		}

		Profile player1 = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(args[0]));

		if (player1 == null) {
			player.message(ErrorMessages.DUEL_SENDER_NOT_ONLINE);
			return;
		}

		if (player1.getPlayerStatus() != PlayerStatus.IDLE) {
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
			MatchData matchData = duelRequest.getMatchData();
			Match match = player1.isInParty() && player.isInParty()
					? new PartyMatch(player1.getParty(), player.getParty(), matchData)
					: new Match(player1, player, matchData);
			match.start();
			return;
		}
	}
}
