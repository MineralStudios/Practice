package ms.uk.eclipse.commands.config;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.rank.RankPower;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.PlayerSettingsManager;
import ms.uk.eclipse.util.messages.ChatMessages;
import ms.uk.eclipse.util.messages.ErrorMessages;
import ms.uk.eclipse.util.messages.UsageMessages;

public class SettingsConfigCommand extends PlayerCommand {

	final PlayerSettingsManager settingsConfig = PracticePlugin.INSTANCE.getSettingsManager();
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public SettingsConfigCommand() {
		super("settingsconfig", RankPower.MANAGER);
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = args.length > 0 ? args[0] : "";
		String toggled;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.SETTINGS_COMMANDS.send(pl);
				ChatMessages.SETTINGS_ENABLE.send(pl);
				ChatMessages.SETTINGS_SLOT.send(pl);
				ChatMessages.SETTINGS_DISPLAY.send(pl);
				return;
			case "enable":
				if (args.length < 3) {
					UsageMessages.SETTINGS_ENABLE.send(pl);
					return;
				}

				toggled = args[1].toLowerCase();

				switch (toggled) {
					case "false":
						settingsConfig.setEnabled(false);
						break;
					case "true":
						settingsConfig.setEnabled(true);
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

				settingsConfig.setDisplayItem(pl.getItemInHand());

				if (args.length > 2) {
					settingsConfig.setDisplayName(args[1].replace("&", "§"));
				}

				ChatMessages.SETTINGS_DISPLAY_SET.send(pl);
				return;
			case "slot":
				if (args.length < 2) {
					UsageMessages.SETTINGS_SLOT.send(pl);
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

				settingsConfig.setSlot(slot);

				ChatMessages.SETTINGS_SLOT_SET.clone().replace("%slot%", strSlot).send(pl);
				return;
		}
	}
}
