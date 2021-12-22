package ms.uk.eclipse.scoreboard;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.board.Board;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;

public class InMatchScoreboard extends Scoreboard {
	PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public InMatchScoreboard(Profile p) {
		super(p);
	}

	@Override
	public void updateBoard(Board board) {
		board.updateLines(CC.BOARD_SEPARATOR, "Opponent: " + CC.SECONDARY + p.getOpponent().getName(),
				"Your Ping: " + CC.SECONDARY + ((CraftPlayer) board.getPlayer()).getHandle().ping,
				"Their Ping: " + CC.SECONDARY + p.getOpponent().bukkit().getHandle().ping, CC.BOARD_SEPARATOR);
	}
}
