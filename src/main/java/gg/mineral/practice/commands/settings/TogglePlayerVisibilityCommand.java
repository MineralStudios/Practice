package gg.mineral.practice.commands.settings;

import java.util.List;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class TogglePlayerVisibilityCommand extends PlayerCommand {

	public TogglePlayerVisibilityCommand() {
		super("toggleplayervisibility");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile profile = PlayerManager.get(p -> p.getUUID().equals(pl.getUniqueId()));
		profile.setPlayersVisible(!profile.getPlayersVisible());

		List<org.bukkit.entity.Player> list = pl.getWorld().getPlayers();
		int i;

		if (profile.getPlayersVisible()) {
			for (i = 0; i < list.size(); i++) {
				org.bukkit.entity.Player p = list.get(i);
				pl.showPlayer(p);
			}
		} else {
			for (i = 0; i < list.size(); i++) {
				org.bukkit.entity.Player p = list.get(i);
				pl.hidePlayer(p);
			}
		}

		ChatMessages.DUEL_REQUESTS_TOGGLED.clone().replace("%toggled%",
				profile.getPlayersVisible() ? "enabled" : "disabled").send(pl);
	}
}
