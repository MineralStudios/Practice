package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.val;

public class TogglePlayerVisibilityCommand extends PlayerCommand {

	public TogglePlayerVisibilityCommand() {
		super("toggleplayervisibility");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		val profile = ProfileManager.getOrCreateProfile(pl);
		profile.setPlayersVisible(!profile.isPlayersVisible());

		profile.updateVisiblity();

		ChatMessages.VISIBILITY_TOGGLED.clone().replace("%toggled%",
				profile.isPlayersVisible() ? "enabled" : "disabled").send(pl);
	}
}
