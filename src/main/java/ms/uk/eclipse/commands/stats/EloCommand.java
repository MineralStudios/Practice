package ms.uk.eclipse.commands.stats;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.inventory.menus.EloMenu;
import ms.uk.eclipse.managers.PlayerManager;

public class EloCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public EloCommand() {
		super("elo");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);

		if (args.length == 0) {
			player.openMenu(new EloMenu(player));
			return;
		}

		Profile playerarg = playerManager.getProfile(args[0]);

		if (playerarg == null) {
			player.openMenu(new EloMenu(args[0]));
			return;
		}

		player.openMenu(new EloMenu(playerarg));

	}
}
