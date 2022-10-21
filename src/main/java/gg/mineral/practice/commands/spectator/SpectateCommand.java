package gg.mineral.practice.commands.spectator;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.SpectateMenu;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class SpectateCommand extends PlayerCommand {

	public SpectateCommand() {
		super("spectate");
		setAliases("spec");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile profile = PlayerManager.get(p -> p.getUUID().equals(pl.getUniqueId()));

		if (profile.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (args.length < 1) {

			profile.openMenu(new SpectateMenu());

			return;
		}

		Profile playerarg = PlayerManager
				.get(p -> p.getName().equals(args[0])
						&& p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (playerarg == null) {
			profile.message(ErrorMessages.PLAYER_NOT_IN_MATCH);
			return;
		}

		profile.spectate(playerarg);
	}
}
