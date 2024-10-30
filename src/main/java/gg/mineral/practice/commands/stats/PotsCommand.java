package gg.mineral.practice.commands.stats;

import org.bukkit.Material;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;

import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.val;

public class PotsCommand extends PlayerCommand {

	public PotsCommand() {
		super("pots");
		setAliases("potions");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		val profile = ProfileManager
				.getProfile(pl.getUniqueId(), p -> p.getPlayerStatus() == PlayerStatus.FIGHTING);

		if (profile == null) {
			pl.sendMessage(ErrorMessages.NOT_IN_MATCH.toString());
			return;
		}

		val pots = profile.getInventory().getNumber(Material.POTION, (short) 16421);
		ChatMessages.POTS.clone().replace("%pots%", "" + pots).send(pl);
	}
}
