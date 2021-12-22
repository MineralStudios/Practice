package ms.uk.eclipse.commands.settings;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;

public class ToggleDuelRequestsCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public ToggleDuelRequestsCommand() {
		super("toggleduelrequests");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);
		player.setRequests(!player.getRequests());
		String ccolor = player.getRequests() ? CC.GREEN : CC.RED;
		player.message(new ChatMessage("Duel requests have been set to " + player.getRequests(), CC.PRIMARY, false)
				.highlightText(ccolor, " " + player.getRequests()));
	}
}
