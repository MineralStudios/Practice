package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.util.messages.CC;

public class KitEditorScoreboard extends DefaultScoreboard {

    public static final DefaultScoreboard INSTANCE = new KitEditorScoreboard();

    @Override
    public void updateBoard(Scoreboard board, Profile profile) {

        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Editing Kit",
                CC.BOARD_SEPARATOR);
    }
}
