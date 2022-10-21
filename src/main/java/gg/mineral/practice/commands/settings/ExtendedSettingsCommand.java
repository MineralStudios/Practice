package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.core.managers.ProfileManager;
import gg.mineral.practice.inventory.menus.ExtendedSettingsMenu;

public class ExtendedSettingsCommand extends PlayerCommand {

	public ExtendedSettingsCommand() {
		super("settings");
	}

	@Override
	public void execute(org.bukkit.entity.Player player, String[] args) {
		ProfileManager.getProfile(player).openMenu(new ExtendedSettingsMenu());
	}
}
