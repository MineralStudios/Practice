package ms.uk.eclipse.util;

import org.bukkit.Bukkit;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.entity.Profile;

public class PearlCooldown {
	final Profile profile;
	Integer time = 0;

	public PearlCooldown(Profile profile) {
		this.profile = profile;
	}

	public void start() {
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (time > 0) {
					time--;
					profile.bukkit().setLevel(time);

					if (time <= 0) {
						profile.bukkit().setLevel(0);
					}
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
