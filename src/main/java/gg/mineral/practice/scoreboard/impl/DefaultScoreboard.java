package gg.mineral.practice.scoreboard.impl;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.scoreboard.Board;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.Setter;

public class DefaultScoreboard {
	Profile profile;
	Board board;
	DefaultScoreboard instance;
	@Setter
	@Getter
	int updateFrequency = 20;

	public DefaultScoreboard(Profile profile) {
		this.profile = profile;
	}

	int taskID;

	public void setBoard() {
		if (profile.getScoreboard() != null)
			profile.getScoreboard().remove();

		Board board;

		try {
			board = new Board(profile.getPlayer());
		} catch (Exception e) {
			return;
		}

		board.updateTitle(CC.PRIMARY + CC.B + "Mineral");
		this.board = board;
		instance = this;
		profile.setScoreboard(this);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (profile.getScoreboard() != null)
					if (profile.getScoreboard().equals(instance))
						updateBoard(board);
					else
						remove();
			}
		}, 0, getUpdateFrequency());
	}

	public void updateBoard(Board board) {
		board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Online: " + CC.SECONDARY + Bukkit.getOnlinePlayers().size(),
				CC.ACCENT + "In Game: " + CC.SECONDARY
						+ ProfileManager.count(p -> p.getPlayerStatus() == PlayerStatus.FIGHTING),
				CC.BOARD_SEPARATOR);
	}

	public void remove() {
		Bukkit.getScheduler().cancelTask(taskID);
		profile.removeScoreboard();
	}

}
