package gg.mineral.practice.commands.stats;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.inventory.menus.EloMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import lombok.val;

public class EloCommand extends PlayerCommand {

	public EloCommand() {
		super("elo");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		val profile = ProfileManager.getOrCreateProfile(pl);

		QueuetypeManager.getQueuetypes().values().stream().filter(Queuetype::isRanked).findAny()
				.ifPresent(queuetype -> {
					if (args.length == 0) {
						profile.openMenu(new EloMenu(profile, queuetype));
						return;
					}

					val eloProfile = ProfileManager.getProfileData(args[0], null);
					profile.openMenu(new EloMenu(eloProfile, queuetype));
				});
	}
}
