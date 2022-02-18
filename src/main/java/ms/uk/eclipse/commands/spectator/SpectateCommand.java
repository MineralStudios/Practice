package ms.uk.eclipse.commands.spectator;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.menus.SpectateMenu;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class SpectateCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public SpectateCommand() {
		super("spectate");
		setAliases("spec");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);

		if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (args.length < 1) {

			player.openMenu(new SpectateMenu());

			return;
		}

		Profile playerarg = playerManager.getProfileFromMatch(args[0]);

		if (playerarg == null) {
			player.message(ErrorMessages.PLAYER_NOT_IN_MATCH);
			return;
		}

		player.spectate(playerarg);
	}
}
