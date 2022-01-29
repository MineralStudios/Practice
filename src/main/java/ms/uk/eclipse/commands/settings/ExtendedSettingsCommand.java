package ms.uk.eclipse.commands.settings;

import ms.uk.eclipse.core.CorePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.entity.Profile;
import ms.uk.eclipse.core.managers.PlayerManager;
import ms.uk.eclipse.inventory.menus.ExtendedSettingsMenu;

public class ExtendedSettingsCommand extends PlayerCommand {
	protected PlayerManager playerManager = CorePlugin.getInstance().getPlayerManager();

	public ExtendedSettingsCommand() {
		super("settings");
	}

	@Override
	public void execute(org.bukkit.entity.Player player, String[] args) {
		Profile p = playerManager.getPlayer(player);
		p.openMenu(new ExtendedSettingsMenu());
	}
}
