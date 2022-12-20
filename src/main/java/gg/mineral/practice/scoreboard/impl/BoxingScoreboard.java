package gg.mineral.practice.scoreboard.impl;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Board;
import gg.mineral.practice.util.messages.CC;

public class BoxingScoreboard extends DefaultScoreboard {

    public BoxingScoreboard(Profile p) {
        super(p);
        setUpdateFrequency(10);
    }

    @Override
    public void updateBoard(Board board) {
        int hitDifference = profile.getMatchStatisticCollector().getHitCount()
                - profile.getOpponent().getMatchStatisticCollector().getHitCount();
        String symbol = "+";
        String color = CC.D_GREEN;

        if (hitDifference < 0) {
            symbol = "";
            color = CC.D_RED;
        }

        board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Opponent: " + CC.SECONDARY + profile.getOpponent().getName(),
                CC.ACCENT + "Your Hits: " + CC.SECONDARY + profile.getMatchStatisticCollector().getHitCount() + color
                        + " (" + symbol + hitDifference
                        + ")",
                CC.ACCENT + "Their Hits: " + CC.SECONDARY
                        + profile.getOpponent().getMatchStatisticCollector().getHitCount(),
                CC.ACCENT + "Your Ping: " + CC.SECONDARY + ((CraftPlayer) board.getPlayer()).getHandle().ping,
                CC.ACCENT + "Their Ping: " + CC.SECONDARY + profile.getOpponent().getPlayer().getHandle().ping,
                CC.BOARD_SEPARATOR);
    }
}
