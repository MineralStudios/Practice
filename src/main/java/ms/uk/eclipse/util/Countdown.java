package ms.uk.eclipse.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.message.ChatMessage;

import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.util.messages.ChatMessages;

public class Countdown {
	int time;
	int taskID;
	ChatMessage message;
	Match match;
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public Countdown(int seconds, Match match) {
		this.time = seconds;
		this.match = match;
	}

	public void start() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

		for (int i = 0; i < match.getParticipants().size(); i++) {
			Profile pl = match.getParticipants().get(i);
			pl.setInMatchCountdown(true);
		}

		taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (time == 0) {
					cancel();
					return;
				}

				message = ChatMessages.BEGINS_IN.clone().replace("%time%", "" + time);

				playerManager.broadcast(match.getParticipants(), message);

				time = time - 1;
			}
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
