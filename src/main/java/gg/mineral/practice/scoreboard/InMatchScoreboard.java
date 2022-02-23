package gg.mineral.practice.scoreboard;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import gg.mineral.core.board.Board;
import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;

public class InMatchScoreboard extends Scoreboard {
	PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public InMatchScoreboard(Profile p) {
		super(p);
	}

	@Override
	public void updateBoard(Board board) {
		board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Opponent: " + CC.SECONDARY + p.getOpponent().getName(),
				CC.ACCENT + "Your Ping: " + CC.SECONDARY + ((CraftPlayer) board.getPlayer()).getHandle().ping,
				CC.ACCENT + "Their Ping: " + CC.SECONDARY + p.getOpponent().bukkit().getHandle().ping,
				CC.BOARD_SEPARATOR);
	}
}
