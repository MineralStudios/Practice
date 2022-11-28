package gg.mineral.practice.commands.config;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class PartiesCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final PartyManager partyManager = PracticePlugin.INSTANCE.getPartyManager();

	public PartiesCommand() {
		super("parties", "practice.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = args.length > 0 ? args[0] : "";
		String toggled;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.PARTIES_COMMANDS.send(pl);
				ChatMessages.PARTIES_ENABLE.send(pl);
				ChatMessages.PARTIES_DISPLAY.send(pl);
				ChatMessages.PARTIES_SLOT.send(pl);
				return;
			case "enable":
				if (args.length < 2) {
					UsageMessages.PARTIES_ENABLE.send(pl);
					return;
				}

				toggled = args[1].toLowerCase();

				switch (toggled) {
					case "false":
						partyManager.setEnabled(false);
						break;
					case "true":
						partyManager.setEnabled(true);
						break;
					default:
						UsageMessages.PARTIES_ENABLE.send(pl);
						return;
				}

				ChatMessages.PARTIES_ENABLED.clone().replace("%toggled%", toggled).send(pl);
				return;
			case "setdisplay":
				if (args.length < 1) {
					UsageMessages.PARTIES_DISPLAY.send(pl);
					return;
				}

				partyManager.setDisplayItem(pl.getItemInHand());

				if (args.length > 2) {
					partyManager.setDisplayName(args[1].replace("&", "§"));
				}

				ChatMessages.PARTIES_DISPLAY_SET.send(pl);
				return;
			case "slot":
				if (args.length < 2) {
					UsageMessages.PARTIES_SLOT.send(pl);
					return;
				}

				int slot;
				String strSlot = args[1];
				try {
					slot = Integer.parseInt(strSlot);
				} catch (Exception e) {
					ErrorMessages.INVALID_SLOT.send(pl);
					return;
				}

				partyManager.setSlot(slot);

				ChatMessages.PARTIES_SLOT_SET.clone().replace("%slot%", strSlot).send(pl);

				return;
		}
	}
}
