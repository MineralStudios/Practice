package gg.mineral.practice.scoreboard.impl;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.data.MatchStatisticCollector;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.util.messages.CC;
import lombok.val;

public class BoxingScoreboard implements Scoreboard {

    public static final Scoreboard INSTANCE = new BoxingScoreboard();

    @Override
    public void updateBoard(ScoreboardHandler board, @NonNull Profile profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral");

        val match = profile.getMatch();

        match.stat(profile.getUuid(), collector -> {
            val opponent = match.getOpponent(profile);

            if (opponent == null)
                return;

            val opponentHitCount = match.computeStat(opponent.getUuid(), MatchStatisticCollector::getHitCount);
            int hitDifference = collector.getHitCount()
                    - opponentHitCount;
            var symbol = "+";
            var color = CC.D_GREEN;

            if (hitDifference < 0) {
                symbol = "";
                color = CC.D_RED;
            }

            board.updateLines(CC.BOARD_SEPARATOR,
                    CC.ACCENT + "Opponent: " + CC.SECONDARY
                            + match.getOpponent(profile).getName(),
                    CC.ACCENT + "Your Hits: " + CC.SECONDARY
                            + collector.getHitCount() + color
                            + " (" + symbol + hitDifference
                            + ")",
                    CC.ACCENT + "Their Hits: " + CC.SECONDARY
                            + opponentHitCount,
                    CC.ACCENT + "Your Ping: " + CC.SECONDARY
                            + ((CraftPlayer) board.getPlayer()).getHandle().ping,
                    CC.ACCENT + "Their Ping: " + CC.SECONDARY
                            + profile.getMatch().getOpponent(profile).getPlayer()
                                    .getHandle().ping,
                    CC.SPACER,
                    CC.SECONDARY + "mineral.gg",
                    CC.BOARD_SEPARATOR);
        });

    }
}
