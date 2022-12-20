package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.EloMenu;
import gg.mineral.practice.managers.ProfileManager;

public class EloCommand extends PlayerCommand {

	public EloCommand() {
		super("elo");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile profile = ProfileManager.getOrCreateProfile(pl);

		if (args.length == 0) {
			profile.openMenu(new EloMenu(profile));
			return;
		}

		Profile eloProfile = ProfileManager.getProfile(p -> p.getName().equalsIgnoreCase(args[0]));

		if (eloProfile == null) {
			profile.openMenu(new EloMenu(args[0]));
			return;
		}

		profile.openMenu(new EloMenu(eloProfile));

	}
}
