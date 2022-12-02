package gg.mineral.practice.util.math;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class Countdown {
	int time;
	int taskID;
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
				Profile pl = match.getParticipants().get(i);
				pl.setInMatchCountdown(true);
			}
		}, 4L);

		taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, () -> {
			if (time == 0) {
				cancel();
				return;
			}

			message = ChatMessages.BEGINS_IN.clone().replace("%time%", "" + time);

			PlayerManager.broadcast(match.getParticipants(), message);

			time = time - 1;
		}, 0L, 20L);
	}

	public void cancel() {

		for (int i = 0; i < match.getParticipants().size(); i++) {
			Profile pl = match.getParticipants().get(i);
			pl.setInMatchCountdown(false);
			pl.message(ChatMessages.MATCH_STARTED);
		}

		Bukkit.getScheduler().cancelTask(taskID);
	}
}
