package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Scoreboard;

public class SpectatorScoreboard extends DefaultScoreboard {
    @Override
    public void updateBoard(Scoreboard board, Profile profile) {

        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Spectating",
                CC.BOARD_SEPARATOR);
    }
}
