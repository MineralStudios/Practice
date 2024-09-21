package gg.mineral.practice.commands.settings;

import java.util.List;

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

		List<org.bukkit.entity.Player> list = pl.getWorld().getPlayers();
		int i;

		if (profile.isPlayersVisible()) {
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
				profile.isPlayersVisible() ? "enabled" : "disabled").send(pl);
	}
}
