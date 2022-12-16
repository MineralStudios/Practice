package gg.mineral.practice.commands.settings;

import java.util.List;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class TogglePlayerVisibilityCommand extends PlayerCommand {

	public TogglePlayerVisibilityCommand() {
		super("toggleplayervisibility");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = ProfileManager.getOrCreateProfile(pl);
		player.setPlayersVisible(!player.isPlayersVisible());

		List<org.bukkit.entity.Player> list = pl.getWorld().getPlayers();
		int i;

		if (player.isPlayersVisible()) {
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

		ChatMessages.VISIBILITY_TOGGLED.clone().replace("%toggled%",
				player.isPlayersVisible() ? "enabled" : "disabled").send(pl);
	}
}
