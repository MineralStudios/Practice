package gg.mineral.practice.commands.config;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.PlayerSettingsManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;
import lombok.val;

public class SettingsConfigCommand extends PlayerCommand {

	public SettingsConfigCommand() {
		super("settingsconfig", "practice.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		val arg = args.length > 0 ? args[0] : "";
		String toggled;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.SETTINGS_COMMANDS.send(pl);
				ChatMessages.SETTINGS_ENABLE.send(pl);
				ChatMessages.SETTINGS_SLOT.send(pl);
				ChatMessages.SETTINGS_DISPLAY.send(pl);
				return;
			case "enable":
				if (args.length < 2) {
					UsageMessages.SETTINGS_ENABLE.send(pl);
					return;
				}

				toggled = args[1].toLowerCase();

				switch (toggled) {
					case "false":
						PlayerSettingsManager.setEnabled(false);
						break;
					case "true":
						PlayerSettingsManager.setEnabled(true);
						break;
					default:
						UsageMessages.SETTINGS_ENABLE.send(pl);
						return;
				}

				ChatMessages.SETTINGS_ENABLED.clone().replace("%toggled%", toggled).send(pl);

				return;
			case "setdisplay":
				if (args.length < 1) {
					UsageMessages.SETTINGS_DISPLAY.send(pl);
					return;
				}

				PlayerSettingsManager.setDisplayItem(pl.getItemInHand());

				if (args.length > 2)
					PlayerSettingsManager.setDisplayName(args[1].replace("&", "§"));

				ChatMessages.SETTINGS_DISPLAY_SET.send(pl);
				return;
			case "slot":
				if (args.length < 2) {
					UsageMessages.SETTINGS_SLOT.send(pl);
					return;
				}

				int slot;
				val strSlot = args[1];
				try {
					slot = Integer.parseInt(strSlot);
				} catch (Exception e) {
					ErrorMessages.INVALID_SLOT.send(pl);
					return;
				}

				PlayerSettingsManager.setSlot(slot);

				ChatMessages.SETTINGS_SLOT_SET.clone().replace("%slot%", strSlot).send(pl);
				return;
		}
	}
}
