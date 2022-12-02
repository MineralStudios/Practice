package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.scoreboard.Board;

public class PartyMatchScoreboard extends DefaultScoreboard {
    PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

    public PartyMatchScoreboard(Profile p) {
        super(p);
    }

    @Override
    public void updateBoard(Board board) {
        Match match = p.getMatch();

        if (match == null) {
            return;
        }

        ProfileList team = match.getTeam(p);
        ProfileList opponents = new ProfileList(match.getParticipants());
        opponents.removeAll(team);

        board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Your Team Remaining: " + CC.SECONDARY + team.size(),
                CC.ACCENT + "Their Team Remaining: " + CC.SECONDARY + opponents.size(), CC.BOARD_SEPARATOR);
    }
}
