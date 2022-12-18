package gg.mineral.practice.scoreboard.impl;

import java.util.List;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.PartyMatch;
import gg.mineral.practice.scoreboard.Board;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;

public class PartyBoxingScoreboard extends DefaultScoreboard {

    public PartyBoxingScoreboard(Profile p) {
        super(p);
    }

    @Override
    public void updateBoard(Board board) {
        if (profile.getMatch() == null) {
            return;
        }

        PartyMatch match = (PartyMatch) profile.getMatch();

        boolean isTeam1 = match.getTeam1RemainingPlayers().contains(profile);
        int hitCount = isTeam1 ? match.getTeam1HitCount() : match.getTeam2HitCount();
        int opponentHitCount = isTeam1 ? match.getTeam2HitCount() : match.getTeam1HitCount();
        int requiredHitCount = isTeam1 ? match.getTeam1RequiredHitCount() : match.getTeam2RequiredHitCount();
        int opponentRequiredHitCount = isTeam1 ? match.getTeam2RequiredHitCount() : match.getTeam1RequiredHitCount();

        List<Profile> team = match.getTeam(profile);
        ProfileList opponents = new ProfileList(match.getParticipants());
        opponents.removeAll(team);

        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Your Team\'s' Hits: " + CC.SECONDARY + hitCount + "/" + requiredHitCount,
                CC.ACCENT + "Their Team\'s' Hits: " + CC.SECONDARY + opponentHitCount + "/" + opponentRequiredHitCount,
                CC.BOARD_SEPARATOR);
    }
}
