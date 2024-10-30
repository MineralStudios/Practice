package gg.mineral.practice.commands.spectator;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;

import gg.mineral.practice.inventory.menus.SpectateMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.val;

public class SpectateCommand extends PlayerCommand {

	public SpectateCommand() {
		super("spectate");
		setAliases("spec");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		val profile = ProfileManager.getOrCreateProfile(pl);

		if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
			profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (args.length < 1) {
			profile.openMenu(new SpectateMenu());
			return;
		}

		val profileToSpectate = ProfileManager
				.getProfile(args[0],
						p -> p.getPlayerStatus() == PlayerStatus.FIGHTING || p.isInEvent());

		if (profileToSpectate == null) {
			profile.message(ErrorMessages.PLAYER_NOT_IN_MATCH_OR_EVENT);
			return;
		}

		profile.getSpectateHandler().spectate(profileToSpectate);
	}
}
