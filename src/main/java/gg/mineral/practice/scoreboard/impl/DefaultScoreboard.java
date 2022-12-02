package gg.mineral.practice.scoreboard.impl;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.scoreboard.Board;

public class DefaultScoreboard {
	Profile p;
	Board b;
	DefaultScoreboard instance;

	int updateFrequency = 20;

	public void setUpdateFrequency(int updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	public int getUpdateFrequency() {
		return updateFrequency;
	}

	public DefaultScoreboard(Profile p) {
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

		b.updateTitle(CC.PRIMARY + CC.B + "Mineral");
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
		}, 0, getUpdateFrequency());
	}

	public void updateBoard(Board board) {
		board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Online: " + CC.SECONDARY + Bukkit.getOnlinePlayers().size(),
				CC.ACCENT + "In Game: " + CC.SECONDARY + PlayerManager.getProfilesInMatch().size(), CC.BOARD_SEPARATOR);
	}

	public void remove() {
		Bukkit.getScheduler().cancelTask(taskID);
		p.removeScoreboard();
	}

}
