package gg.mineral.practice.commands.stats;

import org.bukkit.Material;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class PotsCommand extends PlayerCommand {

	public PotsCommand() {
		super("pots");
		setAliases("potions");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile profile = ProfileManager
				.getProfile(pl.getUniqueId(), p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (profile == null) {
			pl.sendMessage(ErrorMessages.NOT_IN_MATCH.toString());
			return;
		}

		int pots = profile.getInventory().getNumber(Material.POTION, (short) 16421);
		ChatMessages.POTS.clone().replace("%pots%", "" + pots).send(pl);
	}
}
