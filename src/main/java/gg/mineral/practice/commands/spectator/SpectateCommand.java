package gg.mineral.practice.commands.spectator;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.SpectateMenu;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

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
