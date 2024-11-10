package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.MatchStatisticCollector;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;

import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.CC;
import lombok.val;

public class SpectatorScoreboard
                implements Scoreboard {

        public static final Scoreboard INSTANCE = new SpectatorScoreboard();

        @Override
        public void updateBoard(ScoreboardHandler board, Profile profile) {
                board.updateTitle(CC.PRIMARY + CC.B + "Mineral");

                val spectatable = profile.getSpectateHandler().getSpectatable();

                if (spectatable instanceof TeamMatch match) {
                        val team = match.getTeam(match.getProfile1(), true);
                        val opponents = new ProfileList(match.getParticipants());
                        opponents.removeAll(team);

                        if (match.getData().isBoxing()) {

                                int hitCount = match.getTeam1HitCount();
                                int opponentHitCount = match.getTeam2HitCount();
                                int requiredHitCount = match.getTeam1RequiredHitCount();
                                int opponentRequiredHitCount = match.getTeam2RequiredHitCount();

                                board.updateLines(CC.BOARD_SEPARATOR,
                                                CC.SECONDARY + match.getProfile1().getName(),
                                                CC.YELLOW + " * " + CC.ACCENT + "Hits: "
                                                                + CC.WHITE + hitCount + "/" + requiredHitCount,
                                                CC.SECONDARY
                                                                + match.getProfile2().getName(),
                                                CC.YELLOW + " * " + CC.ACCENT + "Hits: " + CC.WHITE
                                                                + opponentHitCount + "/" + opponentRequiredHitCount,
                                                CC.SPACER,
                                                CC.SECONDARY + "mineral.gg",
                                                CC.BOARD_SEPARATOR);
                                return;
                        }

                        board.updateLines(CC.BOARD_SEPARATOR,
                                        CC.SECONDARY + match.getProfile1().getName(),
                                        CC.YELLOW + " * " + CC.ACCENT + "Team Remaining: "
                                                        + CC.WHITE + team.size(),
                                        CC.SECONDARY
                                                        + match.getProfile2().getName(),
                                        CC.YELLOW + " * " + CC.ACCENT + "Team Remaining: " + CC.WHITE
                                                        + opponents.size(),
                                        CC.SPACER,
                                        CC.SECONDARY + "mineral.gg",
                                        CC.BOARD_SEPARATOR);
                } else if (spectatable instanceof Match match) {
                        if (match.getData().isBoxing()) {

                                val profile1 = match.getProfile1();
                                val profile2 = match.getProfile2();

                                val profile1HitCount = match.computeStat(profile1.getUuid(),
                                                MatchStatisticCollector::getHitCount);
                                val profile2HitCount = match.computeStat(profile2.getUuid(),
                                                MatchStatisticCollector::getHitCount);

                                board.updateLines(CC.BOARD_SEPARATOR,
                                                CC.SECONDARY + profile1.getName(),
                                                CC.YELLOW + " * " + CC.ACCENT + "Ping: "
                                                                + CC.WHITE
                                                                + profile1.getPlayer().getHandle().ping,
                                                CC.YELLOW + " * " + CC.ACCENT + "Hits: "
                                                                + CC.WHITE
                                                                + profile1HitCount,
                                                CC.SECONDARY
                                                                + profile2.getName(),
                                                CC.YELLOW + " * " + CC.ACCENT + "Ping: " + CC.WHITE
                                                                + profile2.getPlayer().getHandle().ping,
                                                CC.YELLOW + " * " + CC.ACCENT + "Hits: "
                                                                + CC.WHITE
                                                                + profile2HitCount,
                                                CC.SPACER,
                                                CC.SECONDARY + "mineral.gg",
                                                CC.BOARD_SEPARATOR);
                                return;
                        }

                        board.updateLines(CC.BOARD_SEPARATOR,
                                        CC.SECONDARY + match.getProfile1().getName(),
                                        CC.YELLOW + " * " + CC.ACCENT + "Ping: "
                                                        + CC.WHITE + match.getProfile1().getPlayer().getHandle().ping,
                                        CC.SECONDARY
                                                        + match.getProfile2().getName(),
                                        CC.YELLOW + " * " + CC.ACCENT + "Ping: " + CC.WHITE
                                                        + match.getProfile2().getPlayer().getHandle().ping,
                                        CC.SPACER,
                                        CC.SECONDARY + "mineral.gg",
                                        CC.BOARD_SEPARATOR);
                }
        }
}
