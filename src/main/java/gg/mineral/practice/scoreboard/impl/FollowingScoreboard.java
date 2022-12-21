package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Board;

public class FollowingScoreboard extends DefaultScoreboard {

    public FollowingScoreboard(Profile p) {
        super(p);
    }

    @Override
    public void updateBoard(Board board) {

        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Following: " + CC.SECONDARY + profile.getSpectateHandler().getFollowing().getName(),
                CC.BOARD_SEPARATOR);
    }
}
