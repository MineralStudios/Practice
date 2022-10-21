package gg.mineral.practice.commands.duel;

import java.sql.SQLException;
import java.util.Iterator;

import gg.mineral.practice.commands.PlayerCommand;
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

	public AcceptCommand() {
		super("accept");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile profile = PlayerManager.get(p -> p.getUUID().equals(pl.getUniqueId()));

		if (profile.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (args.length == 0) {
			profile.message(UsageMessages.ACCEPT);
			return;
		}

		Profile profile1 = PlayerManager.get(p -> p.getName().equalsIgnoreCase(args[0]));

		if (profile1 == null) {
			profile.message(ErrorMessages.DUEL_SENDER_NOT_ONLINE);
			return;
		}

		if (profile1.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			profile.message(ErrorMessages.DUEL_SENDER_NOT_IN_LOBBY);
			return;
		}

		Iterator<it.unimi.dsi.fastutil.objects.Object2LongMap.Entry<DuelRequest>> it = profile.getRecievedDuelRequests()
				.entryIterator();

		while (it.hasNext()) {
			DuelRequest duelRequest = it.next().getKey();

			if (!duelRequest.getSender().equals(profile1)) {
				continue;
			}

			it.remove();
			MatchData m = duelRequest.getMatchData();
			Match match = profile1.isInParty() && profile.isInParty()
					? new PartyMatch(profile1.getParty(), profile.getParty(), m)
					: new Match(profile1, profile, m);
			try {
				match.start();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return;
		}
	}
}
