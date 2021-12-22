package ms.uk.eclipse.commands.config;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.SetValueMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.kit.KitEditorManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueuetypeManager;

public class KitEditorCommand extends PlayerCommand {
	final KitEditorManager kitEditorConfig = PracticePlugin.INSTANCE.getKitEditorManager();;
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();

	public KitEditorCommand() {
		super("kiteditor", "practice.permission.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = "";
		if (args.length > 0) {
			arg = args[0];
		}

		Profile player = playerManager.getProfile(pl);
		switch (arg.toLowerCase()) {
			case "enable":
				if (args.length < 2) {
					player.message(new UsageMessage("/kiteditor enable <True/False>"));
					return;
				}

				if (args[1].equalsIgnoreCase("false")) {
					kitEditorConfig.setEnabled(false);
					player.message(new SetValueMessage("Kit editor", "false", CC.RED));
					return;
				}

				if (args[1].equalsIgnoreCase("true")) {
					kitEditorConfig.setEnabled(true);
					player.message(new SetValueMessage("Kit editor", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/kiteditor enable <True/False>"));

				return;
			case "setdisplay":
				if (args.length < 1) {
					player.message(new UsageMessage("/kiteditor setdisplay <DisplayName>"));
					return;
				}

				kitEditorConfig.setDisplayItem(player.getItemInHand());

				if (args.length > 1) {
					String name = args[1].replace("&", "§");
					kitEditorConfig.setDisplayName(name);
				}

				player.message(new SetValueMessage("The kit editor display item", "the item in your hand", CC.PRIMARY));

				return;
			case "slot":
				if (args.length < 2) {
					player.message(new UsageMessage("/kiteditor slot <Slot>"));
					return;
				}

				String strSlot = args[1];
				int slot = Integer.parseInt(strSlot);
				kitEditorConfig.setSlot(slot);

				player.message(new SetValueMessage("The kit editor slot", strSlot, CC.PRIMARY));

				return;
			case "setlocation":
				kitEditorConfig.setLocation(player.bukkit().getLocation());
				player.message(new ChatMessage("The kit editor location has been set", CC.PRIMARY, false));

				return;
			default:
				player.message(new StrikingMessage("Kit Editor Help", CC.PRIMARY, true));
				player.message(new ChatMessage("/kiteditor enable <True/False>", CC.SECONDARY, true));
				player.message(new ChatMessage("/kiteditor infinitehealing <Gametype> <Queuetype> <True/False>",
						CC.SECONDARY, true));
				player.message(new ChatMessage("/kiteditor setdisplay <DisplayName>", CC.SECONDARY, true));
				player.message(new ChatMessage("/kiteditor slot <Slot>", CC.SECONDARY, true));
				player.message(new ChatMessage("/kiteditor setlocation", CC.SECONDARY, true));

				return;
		}
	}
}
