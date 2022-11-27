package gg.mineral.practice.commands.spectator;

import gg.mineral.core.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.ChatMessages;
import gg.mineral.practice.util.messages.ErrorMessages;
import gg.mineral.practice.util.messages.UsageMessages;

public class FollowCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public FollowCommand() {
		super("follow");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);
		if (args.length < 1) {
			player.message(UsageMessages.FOLLOW);
			return;
		}

		if (player.getName().equalsIgnoreCase(args[0])) {
			player.message(ErrorMessages.NOT_FOLLOW_SELF);
			return;
		}

		String playerName = args[0];
		Profile playerarg = playerManager.getProfile(playerName);

		if (playerarg == null) {
			player.message(ErrorMessages.PLAYER_NOT_ONLINE);
			return;
		}

		player.follow(playerarg);
		ChatMessages.FOLLOWING.clone().replace("%player%", playerName).send(pl);
		;

		if (playerarg.getPlayerStatus() == PlayerStatus.FIGHTING) {
			player.spectate(playerarg);
		}
	}
}
