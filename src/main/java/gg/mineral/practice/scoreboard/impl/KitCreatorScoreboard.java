package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.util.messages.CC;

public class KitCreatorScoreboard
        implements Scoreboard {

    public static final Scoreboard INSTANCE = new KitCreatorScoreboard();

    @Override
    public void updateBoard(ScoreboardHandler board, Profile profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral");
        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Creating Kit", CC.SPACER,
                CC.SECONDARY + "mineral.gg",
                CC.BOARD_SEPARATOR);
    }
}
