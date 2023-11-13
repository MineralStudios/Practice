package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.inventory.menus.SettingsMenu;
import gg.mineral.practice.managers.ProfileManager;

public class SettingsCommand extends PlayerCommand {

	public SettingsCommand() {
		super("settings");
	}

	@Override
	public void execute(org.bukkit.entity.Player player, String[] args) {
		ProfileManager.getOrCreateProfile(player).openMenu(new SettingsMenu());
	}
}
