package gg.mineral.practice.util.math;

import org.bukkit.Bukkit;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PearlCooldown {
	private final Profile profile;
	private int timeRemaining = 0;

	public void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (timeRemaining > 0)
				profile.getPlayer().setLevel(--timeRemaining);
		}, 0L, 20L);
	}

	public boolean isActive() {
		return timeRemaining > 0;
	}

	public void setTimeRemaining(int time) {
		this.timeRemaining = time;
		profile.getPlayer().setLevel(time);
	}

}
