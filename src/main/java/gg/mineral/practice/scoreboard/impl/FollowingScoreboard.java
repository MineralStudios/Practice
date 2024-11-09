package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.util.messages.CC;
import lombok.val;

public class FollowingScoreboard
        implements Scoreboard {

    public static final Scoreboard INSTANCE = new FollowingScoreboard();

    @Override
    public void updateBoard(ScoreboardHandler board, Profile profile) {
        board.updateTitle(CC.PRIMARY + CC.B + "Mineral");
        val following = profile.getSpectateHandler().getFollowing();

        if (following == null) 
            return;
        
        board.updateLines(CC.BOARD_SEPARATOR,
                CC.ACCENT + "Following: " + CC.SECONDARY + following.getName(),
                CC.SPACER,
                CC.SECONDARY + "mineral.gg",
                CC.BOARD_SEPARATOR);
    }
}
