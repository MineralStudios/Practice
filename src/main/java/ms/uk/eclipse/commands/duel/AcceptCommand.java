package ms.uk.eclipse.commands.duel;

import java.util.Iterator;
import java.util.Map.Entry;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.match.DuelRequest;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.match.MatchData;
import ms.uk.eclipse.match.PartyMatch;
import ms.uk.eclipse.util.messages.ErrorMessages;
import ms.uk.eclipse.util.messages.UsageMessages;

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
