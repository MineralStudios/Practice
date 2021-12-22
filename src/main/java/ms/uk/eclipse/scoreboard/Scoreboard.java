package ms.uk.eclipse.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.board.Board;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;

public class Scoreboard {
	Profile p;
	Board b;
	Scoreboard instance;
	PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public Scoreboard(Profile p) {
		this.p = p;
	}

	int taskID;

	public void setBoard() {
		if (p.getBoard() != null)
			p.getBoard().remove();
		Board b;

		try {
			b = new Board(p.bukkit());
		} catch (Exception e) {
			return;
		}

		b.updateTitle(CC.B + "Strafe " + CC.GRAY + CC.B + "Land");
		this.b = b;
		instance = this;
		p.setScoreboard(this);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (p.getBoard() != null)
					if (p.getBoard().equals(instance))
						updateBoard(b);
					else
						remove();
			}
		}, 0, 20);
	}

	public void updateBoard(Board board) {
		board.updateLines(CC.BOARD_SEPARATOR, "Online: " + CC.SECONDARY + Bukkit.getOnlinePlayers().size(),
				"In Game: " + CC.SECONDARY + playerManager.getProfilesInMatch().size(), CC.BOARD_SEPARATOR);
	}

	public void remove() {
		Bukkit.getScheduler().cancelTask(taskID);
		p.removeScoreboard();
	}

}
