package gg.mineral.practice.inventory;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public enum SubmitAction {
    DUEL {
        @Override
        public void execute(Profile profile) {
            profile.getRequestHandler().sendDuelRequest(profile.getRequestHandler().getDuelRequestReciever());
        }
    },
    P_SPLIT {

        @Override
        public void execute(Profile profile) {
            Party party = profile.getParty();

            if (!profile.getParty().getPartyLeader().equals(profile)) {
                profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER);
                return;
            }

            if (party.getPartyMembers().size() < 2) {
                profile.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH);
                return;
            }

            PartyMatch partyMatch = new PartyMatch(party, profile.getMatchData());
            partyMatch.start();
        }

    },
    TOURNAMENT {

        @Override
        public void execute(Profile profile) {
            Tournament tournament = new Tournament(profile);
            tournament.start();
        }
    };

    public abstract void execute(Profile profile);
}
