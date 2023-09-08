package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.entity.ProfileData;
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

		ProfileData eloProfile = ProfileManager.getProfileData(args[0], null);
		profile.openMenu(new EloMenu(eloProfile));

	}
}
