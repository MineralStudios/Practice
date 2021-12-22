package ms.uk.eclipse.commands.stats;

import org.bukkit.Material;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.InfoMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;

public class PotsCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public PotsCommand() {
		super("pots");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfileFromMatch(pl);

		if (player == null) {
			pl.sendMessage(new ErrorMessage("You are not in match").toString());
			return;
		}

		int pots = player.getNumber(Material.POTION, (short) 16421);
		player.message(new InfoMessage("You have " + pots + " pots in your inventory"));
	}
}
