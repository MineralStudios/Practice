package gg.mineral.practice.commands.settings;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import lombok.val;

public class ToggleDuelRequestsCommand extends PlayerCommand {

	public ToggleDuelRequestsCommand() {
		super("toggleduelrequests");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		val profile = ProfileManager.getOrCreateProfile(pl);
		profile.getRequestHandler().setDuelRequests(!profile.getRequestHandler().isDuelRequests());
		ChatMessages.DUEL_REQUESTS_TOGGLED.clone()
				.replace("%toggled%", profile.getRequestHandler().isDuelRequests() ? "enabled" : "disabled")
				.send(pl);
	}
}
