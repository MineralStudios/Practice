package gg.mineral.practice.util.math;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class Countdown {
	int time, taskID;
	private final Match<?> match;

	public Countdown(int seconds, Match<?> match) {
		this.time = seconds;
		this.match = match;
	}

	public void start() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

		scheduler.scheduleSyncDelayedTask(PracticePlugin.INSTANCE, () -> {
			for (Profile profile : match.getParticipants()) {
				profile.setInMatchCountdown(true);
				match.onCountdownStart(profile);
			}
		}, 4L);

		taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (time == 0) {
				cancel();
				return;
			}

			ProfileManager.broadcast(match.getParticipants(),
					ChatMessages.BEGINS_IN.clone().replace("%time%", "" + time));
			--time;
		}, 0L, 20L);
	}

	public void cancel() {

		match.onMatchStart();

		for (Profile profile : match.getParticipants()) {
			match.onMatchStart(profile);
			profile.setInMatchCountdown(false);
			profile.message(ChatMessages.MATCH_STARTED);
		}

		Bukkit.getScheduler().cancelTask(taskID);
	}
}
