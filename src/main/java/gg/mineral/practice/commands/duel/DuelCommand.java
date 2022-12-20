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
		Profile profile = ProfileManager.getOrCreateProfile(pl);

		if (args.length == 0) {
			if (!profile.isInParty()) {
				profile.message(UsageMessages.DUEL);
				return;
			}

			profile.openMenu(new OtherPartiesMenu());
			return;
		}

		Profile duelReceiver = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(args[0]));

		if (duelReceiver == null) {
			profile.message(ErrorMessages.PLAYER_NOT_ONLINE);
			return;
		}

		if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
			profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
			return;
		}

		if (profile.equals(duelReceiver)) {
			profile.message(ErrorMessages.YOU_CAN_NOT_DUEL_YOURSELF);
			return;
		}

		if (profile.isInParty()) {
			if (!(duelReceiver.isInParty() && duelReceiver.getParty().getPartyLeader().equals(duelReceiver)
					&& profile.getParty().getPartyLeader().equals(profile))) {
				profile.message(ErrorMessages.PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER);
				return;
			}
		}

		profile.setDuelReciever(duelReceiver);
		profile.openMenu(new SelectModeMenu(SubmitAction.DUEL));
	}
}
