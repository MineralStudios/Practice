package gg.mineral.practice.scoreboard.impl;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.scoreboard.Scoreboard;

public class InMatchScoreboard extends DefaultScoreboard {

	public static final DefaultScoreboard INSTANCE = new InMatchScoreboard();

	@Override
	public void updateBoard(Scoreboard board, Profile profile) {
		board.updateLines(CC.BOARD_SEPARATOR,
				CC.ACCENT + "Opponent: " + CC.SECONDARY + profile.getMatch().getOpponent(profile).getName(),
				CC.ACCENT + "Your Ping: " + CC.SECONDARY + ((CraftPlayer) board.getPlayer()).getHandle().ping,
				CC.ACCENT + "Their Ping: " + CC.SECONDARY
						+ profile.getMatch().getOpponent(profile).getPlayer().getHandle().ping,
				CC.BOARD_SEPARATOR);
	}
}
