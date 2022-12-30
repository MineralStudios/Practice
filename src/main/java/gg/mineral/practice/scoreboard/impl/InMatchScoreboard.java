package gg.mineral.practice.scoreboard.impl;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.scoreboard.ScoreboardHandler;
import gg.mineral.practice.util.messages.CC;

public class InMatchScoreboard
		implements Scoreboard {

	public static final Scoreboard INSTANCE = new InMatchScoreboard();

	@Override
	public void updateBoard(ScoreboardHandler board, Profile profile) {
		board.updateTitle(CC.PRIMARY + CC.B + "Mineral");
		board.updateLines(CC.BOARD_SEPARATOR,
				CC.ACCENT + "Opponent: " + CC.SECONDARY + profile.getMatch().getOpponent(profile).getName(),
				CC.ACCENT + "Your Ping: " + CC.SECONDARY + ((CraftPlayer) board.getPlayer()).getHandle().ping,
				CC.ACCENT + "Their Ping: " + CC.SECONDARY
						+ profile.getMatch().getOpponent(profile).getPlayer().getHandle().ping,
				CC.BOARD_SEPARATOR);
	}
}
