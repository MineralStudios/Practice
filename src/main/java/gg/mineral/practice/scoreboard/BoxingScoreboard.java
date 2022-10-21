package gg.mineral.practice.scoreboard;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import gg.mineral.core.board.Board;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.Profile;

public class BoxingScoreboard extends Scoreboard {

    public BoxingScoreboard(Profile p) {
        super(p);
        setUpdateFrequency(10);
    }

    @Override
    public void updateBoard(Board board) {
        int hitDifference = p.getHitCount() - p.getOpponent().getHitCount();
        String symbol = "+";
        String color = CC.D_GREEN;

        if (hitDifference < 0) {
            symbol = "";
            color = CC.D_RED;
        }

        board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Opponent: " + CC.SECONDARY + p.getOpponent().getName(),
                CC.ACCENT + "Your Hits: " + CC.SECONDARY + p.getHitCount() + color + " (" + symbol + hitDifference
                        + ")",
                CC.ACCENT + "Their Hits: " + CC.SECONDARY + p.getOpponent().getHitCount(),
                CC.ACCENT + "Your Ping: " + CC.SECONDARY + ((CraftPlayer) board.getPlayer()).getHandle().ping,
                CC.ACCENT + "Their Ping: " + CC.SECONDARY + p.getOpponent().bukkit().getHandle().ping,
                CC.BOARD_SEPARATOR);
    }
}
