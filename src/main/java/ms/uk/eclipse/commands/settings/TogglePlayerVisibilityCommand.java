package ms.uk.eclipse.commands.settings;

import java.util.List;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ChatMessages;

public class TogglePlayerVisibilityCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public TogglePlayerVisibilityCommand() {
		super("toggleplayervisibility");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfile(pl);
		player.setPlayersVisible(!player.getPlayersVisible());

		List<org.bukkit.entity.Player> list = pl.getWorld().getPlayers();
		int i;

		if (player.getPlayersVisible()) {
			for (i = 0; i < list.size(); i++) {
				org.bukkit.entity.Player p = list.get(i);
				pl.showPlayer(p);
			}
		} else {
			for (i = 0; i < list.size(); i++) {
				org.bukkit.entity.Player p = list.get(i);
				pl.hidePlayer(p, false);
			}
		}

		ChatMessages.DUEL_REQUESTS_TOGGLED.clone().replace("%toggled%",
				player.getPlayersVisible() ? "enabled" : "disabled").send(pl);
	}
}
