package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class ToggleDuelRequestsCommand extends PlayerCommand {

	public ToggleDuelRequestsCommand() {
		super("toggleduelrequests");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = PlayerManager.getProfile(pl);
		player.setRequests(!player.getRequests());
		ChatMessages.DUEL_REQUESTS_TOGGLED.clone().replace("%toggled%", player.getRequests() ? "enabled" : "disabled")
				.send(pl);
		;
	}
}
