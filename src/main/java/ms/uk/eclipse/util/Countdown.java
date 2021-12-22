package ms.uk.eclipse.util;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.match.Match;

public class Countdown {
	int time;
	int taskID;
	ChatMessage message;
	ProfileList players;
	Match match;
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public Countdown(int seconds, Match match) {
		this.time = seconds;
		this.players = match.getParticipants();
		this.match = match;
	}

	public void start() {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(PracticePlugin.INSTANCE, new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < players.size(); i++) {
					Profile pl = players.get(i);
					pl.setInMatchCountdown(true);
				}
			}
		}, 2L);

		taskID = scheduler.scheduleSyncRepeatingTask(PracticePlugin.INSTANCE, new Runnable() {
			@Override
			public void run() {
				if (time == 0) {
					cancel();
					return;
				}

				message = new ChatMessage("The game will begin in " + time + " second(s)", CC.SECONDARY, false)
						.highlightText(CC.PRIMARY, " " + time);

				playerManager.broadcast(players, message);

				time = time - 1;
			}
		}, 0L, 20L);
	}

	public void cancel() {
		StrikingMessage m = new StrikingMessage("The match has started", CC.PRIMARY, true);

		for (int i = 0; i < players.size(); i++) {
			Profile pl = players.get(i);
			pl.setInMatchCountdown(false);
			pl.message(m);
		}

		Bukkit.getScheduler().cancelTask(taskID);
	}
}
