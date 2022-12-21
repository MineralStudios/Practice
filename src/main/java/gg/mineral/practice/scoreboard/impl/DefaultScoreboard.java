package gg.mineral.practice.scoreboard.impl;

import org.bukkit.Bukkit;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.scoreboard.Scoreboard;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.Setter;

public class DefaultScoreboard {
	@Setter
	@Getter
	int updateFrequency = 20;
	int taskID;

	public void setBoard(Profile profile) {

		try {
			Scoreboard board = new Scoreboard(profile.getPlayer());

			board.updateTitle(CC.PRIMARY + CC.B + "Mineral");

			taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
				if (profile.getScoreboard() != null
						&& profile.getScoreboard().equals(DefaultScoreboard.this))
					updateBoard(board, profile);
				else
					remove(profile);
			}, 0, getUpdateFrequency());
		} catch (Exception e) {
		}
	}

	public void updateBoard(Scoreboard board, Profile profile) {
		board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Online: " + CC.SECONDARY + Bukkit.getOnlinePlayers().size(),
				CC.ACCENT + "In Game: " + CC.SECONDARY
						+ ProfileManager.count(p -> p.getPlayerStatus() == PlayerStatus.FIGHTING),
				CC.BOARD_SEPARATOR);
	}

	public void remove(Profile profile) {
		Bukkit.getScheduler().cancelTask(taskID);
		profile.removeScoreboard();
	}

}
