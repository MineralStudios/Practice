package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;

public class PartyMatchScoreboard
        implements Scoreboard {

    public static final Scoreboard INSTANCE = new PartyMatchScoreboard();

    @Override
    public void updateBoard(ScoreboardHandler board, Profile profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral");
        Match match = profile.getMatch();

        if (match == null) {
            return;
        }

        ProfileList team = match.getTeam(profile);
        ProfileList opponents = new ProfileList(match.getParticipants());
        opponents.removeAll(team);

        board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Your Team Remaining: " + CC.SECONDARY + team.size(),
                CC.ACCENT + "Their Team Remaining: " + CC.SECONDARY + opponents.size(), CC.SPACER,
                CC.SECONDARY + "mineral.gg", CC.BOARD_SEPARATOR);
    }
}
