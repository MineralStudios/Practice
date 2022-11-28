package gg.mineral.practice.commands.config;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.kit.KitEditorManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.util.messages.ChatMessages;
import gg.mineral.practice.util.messages.ErrorMessages;
import gg.mineral.practice.util.messages.UsageMessages;

public class KitEditorCommand extends PlayerCommand {
	final KitEditorManager kitEditorManager = PracticePlugin.INSTANCE.getKitEditorManager();;
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();

	public KitEditorCommand() {
		super("kiteditor", "practice.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player player, String[] args) {

		String arg = args.length > 0 ? args[0] : "";

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.KIT_EDITOR_COMMANDS.send(player);
				ChatMessages.KIT_EDITOR_ENABLE.send(player);
				ChatMessages.KIT_EDITOR_DISPLAY.send(player);
				ChatMessages.KIT_EDITOR_SLOT.send(player);
				ChatMessages.KIT_EDITOR_LOCATION.send(player);

				return;
			case "enable":
				if (args.length < 2) {
					UsageMessages.KIT_EDITOR_ENABLE.send(player);
					return;
				}

				String toggled = args[1].toLowerCase();

				switch (toggled) {
					case "false":
						kitEditorManager.setEnabled(false);
						break;
					case "true":
						kitEditorManager.setEnabled(true);
						break;
					default:
						UsageMessages.KIT_EDITOR_ENABLE.send(player);
						return;
				}

				ChatMessages.KIT_EDITOR_ENABLED.clone()
						.replace("%toggled%", toggled).send(player);

				return;
			case "setdisplay":
				if (args.length < 1) {
					UsageMessages.KIT_EDITOR_DISPLAY.send(player);
					return;
				}

				kitEditorManager.setDisplayItem(player.getItemInHand());

				if (args.length > 1) {
					String name = args[1].replace("&", "§");
					kitEditorManager.setDisplayName(name);
				}

				ChatMessages.KIT_EDITOR_DISPLAY_SET.send(player);

				return;
			case "slot":
				if (args.length < 2) {
					UsageMessages.KIT_EDITOR_SLOT.send(player);
					return;
				}

				int slot;
				String strSlot = args[1];
				try {
					slot = Integer.parseInt(strSlot);
				} catch (Exception e) {
					ErrorMessages.INVALID_SLOT.send(player);
					return;
				}

				kitEditorManager.setSlot(slot);

				ChatMessages.KIT_EDITOR_SLOT_SET.clone().replace("%slot%", strSlot).send(player);
				;

				return;
			case "setlocation":
				kitEditorManager.setLocation(player.getLocation());
				ChatMessages.KIT_EDITOR_LOCATION_SET.send(player);

				return;
		}
	}
}
