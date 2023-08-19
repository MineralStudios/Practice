package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.util.messages.CC;

public class TeamBoxingScoreboard
        implements Scoreboard {
    public static final Scoreboard INSTANCE = new TeamBoxingScoreboard();

    @Override
    public void updateBoard(ScoreboardHandler board, Profile profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral");
        if (profile.getMatch() == null) {
            return;
        }

        TeamMatch match = (TeamMatch) profile.getMatch();

        boolean isTeam1 = match.getTeam1RemainingPlayers().contains(profile);
        int hitCount = isTeam1 ? match.getTeam1HitCount() : match.getTeam2HitCount();
        int opponentHitCount = isTeam1 ? match.getTeam2HitCount() : match.getTeam1HitCount();
        int requiredHitCount = isTeam1 ? match.getTeam1RequiredHitCount() : match.getTeam2RequiredHitCount();
        int opponentRequiredHitCount = isTeam1 ? match.getTeam2RequiredHitCount() : match.getTeam1RequiredHitCount();

        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Your Hits: " + CC.SECONDARY + hitCount + "/" + requiredHitCount,
                CC.ACCENT + "Their Hits: " + CC.SECONDARY + opponentHitCount + "/" + opponentRequiredHitCount,
                CC.SPACER,
                CC.SECONDARY + "mineral.gg",
                CC.BOARD_SEPARATOR);
    }
}
