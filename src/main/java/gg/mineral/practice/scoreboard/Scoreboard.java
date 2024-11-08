package gg.mineral.practice.scoreboard;

import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.practice.entity.Profile;

public interface Scoreboard {
    public void updateBoard(ScoreboardHandler board, @NonNull Profile profile);
}
