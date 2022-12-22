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
	public static final DefaultScoreboard INSTANCE = new DefaultScoreboard();

	public int setBoard(Profile profile) {

		profile.getBoard().updateTitle(CC.PRIMARY + CC.B + "Mineral");

		return Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (profile.getScoreboard() != null
					&& profile.getScoreboard().equals(DefaultScoreboard.this))
				updateBoard(profile.getBoard(), profile);
			else {
				profile.removeScoreboard();
			}
		}, 0, getUpdateFrequency());
	}

	public void updateBoard(Scoreboard board, Profile profile) {
		board.updateLines(CC.BOARD_SEPARATOR, CC.ACCENT + "Online: " + CC.SECONDARY + Bukkit.getOnlinePlayers().size(),
				CC.ACCENT + "In Game: " + CC.SECONDARY
						+ ProfileManager.count(p -> p.getPlayerStatus() == PlayerStatus.FIGHTING),
				CC.BOARD_SEPARATOR);
	}
}
