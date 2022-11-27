package gg.mineral.practice.util;

import org.bukkit.Bukkit;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;

public class PearlCooldown {
	final Profile profile;
	Integer time = 0;

	public PearlCooldown(Profile profile) {
		this.profile = profile;
	}

	public void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (time > 0) {
				time--;
				profile.bukkit().setLevel(time);

				if (time <= 0) {
					profile.bukkit().setLevel(0);
				}
			}
		}, 0L, 20L);
	}

	public int getTimeRemaining() {
		return time;
	}

	public boolean isActive() {
		return time > 0;
	}

	public void setTimeRemaining(int time) {
		this.time = time;
		profile.bukkit().setLevel(time);
	}

}
