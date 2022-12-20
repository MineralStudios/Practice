package gg.mineral.practice.commands.duel;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.inventory.menus.OtherPartiesMenu;
import gg.mineral.practice.inventory.menus.SelectModeMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class DuelCommand extends PlayerCommand {

	public DuelCommand() {
		super("duel");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = ProfileManager.getOrCreateProfile(pl);

		if (args.length == 0) {
			if (!player.isInParty()) {
				player.message(UsageMessages.DUEL);
				return;
			}

			player.openMenu(new OtherPartiesMenu());
			return;
		}

		Profile playerarg = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(args[0]));

		if (playerarg == null) {
			player.message(ErrorMessages.PLAYER_NOT_ONLINE);
			return;
		}

		if (player.getPlayerStatus() != PlayerStatus.IDLE) {
			player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (player.equals(playerarg)) {
			player.message(ErrorMessages.YOU_CAN_NOT_DUEL_YOURSELF);
			return;
		}

		if (player.isInParty()) {
			if (!(playerarg.isInParty() && playerarg.getParty().getPartyLeader().equals(playerarg)
					&& player.getParty().getPartyLeader().equals(player))) {
				player.message(ErrorMessages.PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER);
				return;
			}
		}

		player.setDuelReciever(playerarg);
		player.openMenu(new SelectModeMenu(SubmitAction.DUEL));
	}
}
