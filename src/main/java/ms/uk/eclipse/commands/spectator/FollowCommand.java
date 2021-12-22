package ms.uk.eclipse.commands.spectator;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.InfoMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class FollowCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public FollowCommand() {
		super("follow");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);
		if (args.length < 1) {
			player.message(new UsageMessage("/follow <Player>"));
			return;
		}

		if (player.getName().equalsIgnoreCase(args[0])) {
			player.message(new ErrorMessage("You can not follow yourself"));
			return;
		}

		Profile playerarg = playerManager.getProfile(args[0]);

		if (playerarg == null) {
			player.message(ErrorMessages.PLAYER_NOT_ONLINE);
			return;
		}

		player.follow(playerarg);
		player.message(new InfoMessage("You are now following " + args[0]));

		if (playerarg.getPlayerStatus() == PlayerStatus.FIGHTING) {
			player.spectate(playerarg);
		}
	}
}
