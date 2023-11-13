package gg.mineral.practice.util.math;

import org.bukkit.Bukkit;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import lombok.Getter;

public class PearlCooldown {
	final Profile profile;
	@Getter
	Integer timeRemaining = 0;

	public PearlCooldown(Profile profile) {
		this.profile = profile;
	}

	public void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (timeRemaining > 0) {
				timeRemaining--;
				profile.getPlayer().setLevel(timeRemaining);

				if (timeRemaining <= 0) {
					profile.getPlayer().setLevel(0);
				}
			}
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
