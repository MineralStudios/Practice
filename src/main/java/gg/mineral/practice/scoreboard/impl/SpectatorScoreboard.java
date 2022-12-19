package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Board;

public class SpectatorScoreboard extends DefaultScoreboard {

    public SpectatorScoreboard(Profile p) {
        super(p);
    }

    @Override
    public void updateBoard(Board board) {

        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Spectating: ", CC.SECONDARY + profile.getSpectatingMatch().getProfile1().getName(),
                CC.SECONDARY + profile.getSpectatingMatch().getProfile2().getName(),
                CC.BOARD_SEPARATOR);
    }
}
