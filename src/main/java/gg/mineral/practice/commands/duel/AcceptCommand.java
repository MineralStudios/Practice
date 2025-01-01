package gg.mineral.practice.commands.duel;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;
import lombok.val;

public class AcceptCommand extends PlayerCommand {

    public AcceptCommand() {
        super("accept");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        val profile = ProfileManager.getOrCreateProfile(pl);

        if (profile.getPlayerStatus() == PlayerStatus.QUEUEING)
            profile.removeFromQueue();

        if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        if (args.length == 0) {
            profile.message(UsageMessages.ACCEPT);
            return;
        }

        val duelSender = ProfileManager.getProfile(args[0]);

        if (duelSender == null) {
            profile.message(ErrorMessages.DUEL_SENDER_NOT_ONLINE);
            return;
        }

        if (duelSender.getPlayerStatus() == PlayerStatus.QUEUEING)
            duelSender.removeFromQueue();

        if (duelSender.getPlayerStatus() != PlayerStatus.IDLE) {
            profile.message(ErrorMessages.DUEL_SENDER_NOT_IN_LOBBY);
            return;
        }

        if (duelSender.isInParty() != profile.isInParty()) {
            if (profile.isInParty())
                profile.message(ErrorMessages.PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER);
            else
                profile.message(ErrorMessages.PLAYER_IN_PARTY);
            return;
        }

        val it = profile.getRequestHandler().getRecievedDuelRequests().entryIterator();

        while (it.hasNext()) {
            val duelRequest = it.next().getKey();

            if (!duelRequest.sender().equals(duelSender))
                continue;

            it.remove();
            val matchData = new MatchData(duelRequest.duelSettings());
            val match = duelSender.isInParty() && profile.isInParty()
                    ? new TeamMatch(duelSender.getParty(), profile.getParty(), matchData)
                    : new Match(duelSender, profile, matchData);
            match.start();
            return;
        }
    }
}
