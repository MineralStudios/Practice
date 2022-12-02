package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.inventory.menus.ExtendedSettingsMenu;
import gg.mineral.practice.managers.PlayerManager;

public class ExtendedSettingsCommand extends PlayerCommand {

	public ExtendedSettingsCommand() {
		super("settings");
	}

	@Override
	public void execute(org.bukkit.entity.Player player, String[] args) {
		PlayerManager.getProfile(player).openMenu(new ExtendedSettingsMenu());
	}
}
