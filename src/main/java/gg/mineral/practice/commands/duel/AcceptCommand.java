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
		Profile profile = ProfileManager.getOrCreateProfile(pl);

		if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
			profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (args.length == 0) {
			profile.message(UsageMessages.ACCEPT);
			return;
		}

		Profile duelSender = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(args[0]));

		if (duelSender == null) {
			profile.message(ErrorMessages.DUEL_SENDER_NOT_ONLINE);
			return;
		}

		if (duelSender.getPlayerStatus() != PlayerStatus.IDLE) {
			profile.message(ErrorMessages.DUEL_SENDER_NOT_IN_LOBBY);
			return;
		}

		Iterator<Entry<DuelRequest, Long>> it = profile.getRecievedDuelRequests().entryIterator();

		while (it.hasNext()) {
			DuelRequest duelRequest = it.next().getKey();

			if (!duelRequest.getSender().equals(duelSender)) {
				continue;
			}

			it.remove();
			MatchData matchData = duelRequest.getMatchData();
			Match match = duelSender.isInParty() && profile.isInParty()
					? new PartyMatch(duelSender.getParty(), profile.getParty(), matchData)
					: new Match(duelSender, profile, matchData);
			match.start();
			return;
		}
	}
}
