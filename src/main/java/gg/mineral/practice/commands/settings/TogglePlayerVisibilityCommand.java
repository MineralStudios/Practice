package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class TogglePlayerVisibilityCommand extends PlayerCommand {

	public TogglePlayerVisibilityCommand() {
		super("toggleplayervisibility", "practice.admin");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile profile = ProfileManager.getOrCreateProfile(pl);
		profile.setPlayersVisible(!profile.isPlayersVisible());

		profile.updateVisiblity();

		ChatMessages.VISIBILITY_TOGGLED.clone().replace("%toggled%",
				profile.isPlayersVisible() ? "enabled" : "disabled").send(pl);
	}
}
