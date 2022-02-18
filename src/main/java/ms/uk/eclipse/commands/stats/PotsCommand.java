package ms.uk.eclipse.commands.stats;

import org.bukkit.Material;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;

import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ChatMessages;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class PotsCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public PotsCommand() {
		super("pots");
		setAliases("potions");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfileFromMatch(pl);

		if (player == null) {
			pl.sendMessage(ErrorMessages.NOT_IN_MATCH.toString());
			return;
		}

		int pots = player.getNumber(Material.POTION, (short) 16421);
		ChatMessages.POTS.clone().replace("%pots%", "" + pots).send(pl);
	}
}
