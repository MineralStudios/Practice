package gg.mineral.practice.scoreboard.impl;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.util.messages.CC;

public class DefaultScoreboard implements Scoreboard {
	public static final Scoreboard INSTANCE = new DefaultScoreboard();

	@Override
	public void updateBoard(ScoreboardHandler board, Profile profile) {
		board.updateTitle(CC.PRIMARY + CC.B + "Mineral");
		board.updateLines(CC.BOARD_SEPARATOR,
				CC.ACCENT + "Online: " + CC.SECONDARY
						+ ProfileManager.getProfiles().size(),
				CC.ACCENT + "Bots: " + CC.SECONDARY
						+ ProfileManager.countBots(),
				CC.ACCENT + "In Game: " + CC.SECONDARY
						+ ProfileManager.count(p -> p.getPlayerStatus() == PlayerStatus.FIGHTING),
				CC.SPACER,
				CC.SECONDARY + "mineral.gg",
				CC.BOARD_SEPARATOR);
	}
}
