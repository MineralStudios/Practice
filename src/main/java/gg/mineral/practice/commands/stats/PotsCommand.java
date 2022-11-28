package gg.mineral.practice.commands.stats;

import org.bukkit.Material;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.ChatMessages;
import gg.mineral.practice.util.messages.ErrorMessages;

public class PotsCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public PotsCommand() {
		super("pots");
		setAliases("potions");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {
		Profile player = playerManager.getProfileFromMatch(pl);

		if (player == null) {
			pl.sendMessage(ErrorMessages.NOT_IN_MATCH.toString());
			return;
		}

		int pots = player.getNumber(Material.POTION, (short) 16421);
		ChatMessages.POTS.clone().replace("%pots%", "" + pots).send(pl);
	}
}
