package gg.mineral.practice.commands.duel;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.inventory.menus.OtherPartiesMenu;
import gg.mineral.practice.inventory.menus.SelectModeMenu;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class DuelCommand extends PlayerCommand {

	public DuelCommand() {
		super("duel");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile profile = PlayerManager.get(p -> p.getUUID().equals(pl.getUniqueId()));

		if (args.length == 0) {
			if (!profile.isInParty()) {
				profile.message(UsageMessages.DUEL);
				return;
			}

			profile.openMenu(new OtherPartiesMenu());
			return;
		}

		Profile playerarg = PlayerManager.get(p -> p.getName().equalsIgnoreCase(args[0]));

		if (playerarg == null) {
			profile.message(ErrorMessages.PLAYER_NOT_ONLINE);
			return;
		}

		if (profile.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
			profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (profile.equals(playerarg)) {
			profile.message(ErrorMessages.YOU_CAN_NOT_DUEL_YOURSELF);
			return;
		}

		if (profile.isInParty()) {
			if (!(playerarg.isInParty() && playerarg.getParty().getPartyLeader().equals(playerarg)
					&& profile.getParty().getPartyLeader().equals(profile))) {
				profile.message(ErrorMessages.PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER);
				return;
			}
		}

		profile.setDuelReciever(playerarg);
		profile.openMenu(new SelectModeMenu(SubmitAction.DUEL));
	}
}
