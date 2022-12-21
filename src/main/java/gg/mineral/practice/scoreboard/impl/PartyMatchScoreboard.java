package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;

import java.util.List;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.scoreboard.Scoreboard;

public class PartyMatchScoreboard extends DefaultScoreboard {
    @Override
    public void updateBoard(Scoreboard board, Profile profile) {
        Match match = profile.getMatch();

        if (match == null) {
            return;
        }

        List<Profile> team = match.getTeam(profile);
        ProfileList opponents = new ProfileList(match.getParticipants());
        opponents.removeAll(team);

        board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Your Team Remaining: " + CC.SECONDARY + team.size(),
                CC.ACCENT + "Their Team Remaining: " + CC.SECONDARY + opponents.size(), CC.BOARD_SEPARATOR);
    }
}
