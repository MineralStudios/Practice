package ms.uk.eclipse.commands.duel;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.SubmitAction;
import ms.uk.eclipse.inventory.menus.OtherPartiesMenu;
import ms.uk.eclipse.inventory.menus.SelectModeMenu;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class DuelCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public DuelCommand() {
		super("duel");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);

		if (args.length == 0) {
			if (!player.isInParty()) {
				player.message(new UsageMessage("/duel <Player>"));
				return;
			}

			player.openMenu(new OtherPartiesMenu());
			return;
		}

		Profile playerarg = playerManager.getProfile(args[0]);

		if (playerarg == null) {
			player.message(ErrorMessages.PLAYER_NOT_ONLINE);
			return;
		}

		if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
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
