package gg.mineral.practice.util.math;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class Countdown {
	int time, taskID;
	ChatMessage message;
	Match match;

	public Countdown(int seconds, Match match) {
		this.time = seconds;
		this.match = match;
	}

	public void start() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

		scheduler.scheduleSyncDelayedTask(PracticePlugin.INSTANCE, () -> {
			for (int i = 0; i < match.getParticipants().size(); i++) {
				Profile profile = match.getParticipants().get(i);
				profile.setInMatchCountdown(true);
			}
		}, 4L);

		taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (time == 0) {
				cancel();
				return;
			}

			message = ChatMessages.BEGINS_IN.clone().replace("%time%", "" + time);

			ProfileManager.broadcast(match.getParticipants(), message);

			time = time - 1;
		}, 0L, 20L);
	}

	public void cancel() {

		for (int i = 0; i < match.getParticipants().size(); i++) {
			Profile profile = match.getParticipants().get(i);
			profile.setInMatchCountdown(false);
			profile.message(ChatMessages.MATCH_STARTED);
		}

		Bukkit.getScheduler().cancelTask(taskID);
	}
}
