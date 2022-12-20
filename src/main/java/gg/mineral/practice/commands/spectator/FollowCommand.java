package gg.mineral.practice.commands.spectator;

import gg.mineral.practice.commands.PlayerCommand;
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
		Profile profile = ProfileManager.getOrCreateProfile(pl);
		if (args.length < 1) {
			profile.message(UsageMessages.FOLLOW);
			return;
		}

		if (profile.getName().equalsIgnoreCase(args[0])) {
			profile.message(ErrorMessages.NOT_FOLLOW_SELF);
			return;
		}

		String playerName = args[0];
		Profile profileToFollow = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(playerName));

		if (profileToFollow == null) {
			profile.message(ErrorMessages.PLAYER_NOT_ONLINE);
			return;
		}

		profile.follow(profileToFollow);
		ChatMessages.FOLLOWING.clone().replace("%player%", playerName).send(pl);
	}
}
