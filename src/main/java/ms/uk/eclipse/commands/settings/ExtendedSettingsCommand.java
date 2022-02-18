package ms.uk.eclipse.commands.settings;

import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.managers.ProfileManager;
import ms.uk.eclipse.inventory.menus.ExtendedSettingsMenu;

public class ExtendedSettingsCommand extends PlayerCommand {

	public ExtendedSettingsCommand() {
		super("settings");
	}

	@Override
	public void execute(org.bukkit.entity.Player player, String[] args) {
		ProfileManager.getProfile(player).openMenu(new ExtendedSettingsMenu());
	}
}
