package ms.uk.eclipse.commands.config;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.SetValueMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.PlayerSettingsManager;

public class SettingsConfigCommand extends PlayerCommand {

	final PlayerSettingsManager settingsConfig = PracticePlugin.INSTANCE.getSettingsManager();
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public SettingsConfigCommand() {
		super("settingsconfig", "practice.permission.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = "";
		if (args.length > 0) {
			arg = args[0];
		}

		Profile player = playerManager.getProfile(pl);
		switch (arg.toLowerCase()) {
			default:
				player.message(new StrikingMessage("Settings Config", CC.PRIMARY, true));
				player.message(new ChatMessage("/settingsconfig enable <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/settingsconfig slot <Slot>", CC.SECONDARY, false));
				player.message(new ChatMessage("/settingsconfig setdisplay <DisplayName>", CC.SECONDARY, false));

				return;
			case "enable":
				if (args.length < 3) {
					player.message(new UsageMessage("/settingsconfig  enable <True/False>"));
					return;
				}

				if (args[1].equalsIgnoreCase("false")) {
					settingsConfig.setEnabled(false);
					player.message(new SetValueMessage("Player settings", "false", CC.RED));
					return;
				}

				if (args[1].equalsIgnoreCase("true")) {
					settingsConfig.setEnabled(true);
					player.message(new SetValueMessage("Player settings", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/settingsconfig enable <True/False>"));

				return;
			case "setdisplay":
				if (args.length < 2) {
					player.message(new UsageMessage("/settingsconfig setdisplay <DisplayName>"));
					return;
				}

				settingsConfig.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					settingsConfig.setDisplayName(args[1].replace("&", "§"));
				}

				player.message(new ChatMessage("The display item for player settings has been set", CC.PRIMARY, false));

				return;
			case "slot":
				if (args.length < 2) {
					player.message(new UsageMessage("/settingsconfig slot <Slot>"));
					return;
				}

				settingsConfig.setSlot(Integer.parseInt(args[1]));
				player.message(new ChatMessage("The slot for player settings has been set", CC.PRIMARY, false));

				return;
		}
	}
}
