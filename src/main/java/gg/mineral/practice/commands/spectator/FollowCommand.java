package gg.mineral.practice.commands.spectator;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class FollowCommand extends PlayerCommand {

	public FollowCommand() {
		super("follow");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = ProfileManager.getOrCreateProfile(pl);
		if (args.length < 1) {
			player.message(UsageMessages.FOLLOW);
			return;
		}

		if (player.getName().equalsIgnoreCase(args[0])) {
			player.message(ErrorMessages.NOT_FOLLOW_SELF);
			return;
		}

		String playerName = args[0];
		Profile playerarg = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(playerName));

		if (playerarg == null) {
			player.message(ErrorMessages.PLAYER_NOT_ONLINE);
			return;
		}

		player.follow(playerarg);
		ChatMessages.FOLLOWING.clone().replace("%player%", playerName).send(pl);

		if (playerarg.getPlayerStatus() == PlayerStatus.FIGHTING) {
			player.spectate(playerarg);
		}
	}
}
