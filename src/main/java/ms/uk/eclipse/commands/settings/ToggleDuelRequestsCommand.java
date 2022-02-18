package ms.uk.eclipse.commands.settings;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ChatMessages;

public class ToggleDuelRequestsCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public ToggleDuelRequestsCommand() {
		super("toggleduelrequests");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);
		player.setRequests(!player.getRequests());
		ChatMessages.DUEL_REQUESTS_TOGGLED.clone().replace("%toggled%", player.getRequests() ? "enabled" : "disabled")
				.send(pl);
		;
	}
}
