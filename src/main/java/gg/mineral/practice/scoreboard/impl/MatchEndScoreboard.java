package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Board;

public class MatchEndScoreboard extends DefaultScoreboard {

    public MatchEndScoreboard(Profile p) {
        super(p);
    }

    @Override
    public void updateBoard(Board board) {

        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Match Ended",
                CC.BOARD_SEPARATOR);
    }
}
