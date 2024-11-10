package gg.mineral.practice.util.math;

import org.bukkit.Bukkit;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class Countdown {
	int time, taskID;
	private final Match match;

	public Countdown(int seconds, Match match) {
		this(match);
		this.time = seconds;
	}

	public void start() {
		val scheduler = Bukkit.getServer().getScheduler();
		for (val profile : match.getParticipants())
			profile.setInMatchCountdown(true);
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

		for (val profile : match.getParticipants()) {
			match.onMatchStart(profile);
			profile.setInMatchCountdown(false);
			profile.message(ChatMessages.MATCH_STARTED);
		}

		Bukkit.getScheduler().cancelTask(taskID);
	}
}
