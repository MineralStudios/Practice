package ms.uk.eclipse.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.entity.Profile;

public class PearlCooldown {
	int taskID;
	Profile player;

	public PearlCooldown(int seconds, Profile player) {
		player.setPearlCooldown(seconds);
		this.player = player;
	}

	public void start() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, new Runnable() {
			@Override
			public void run() {

				if (player.getPearlCooldown() == 0) {
					cancel();
					return;
				}

				player.setPearlCooldown(player.getPearlCooldown() - 1);
			}
		}, 0L, 20L);
	}

	public void cancel() {
		Bukkit.getScheduler().cancelTask(taskID);
	}
}
