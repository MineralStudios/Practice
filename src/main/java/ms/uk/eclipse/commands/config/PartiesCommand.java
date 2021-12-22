package ms.uk.eclipse.commands.config;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.SetValueMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PartyManager;
import ms.uk.eclipse.managers.PlayerManager;

public class PartiesCommand extends PlayerCommand {
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final PartyManager partyManager = PracticePlugin.INSTANCE.getPartyManager();

	public PartiesCommand() {
		super("parties", "practice.permission.config");
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
				player.message(new StrikingMessage("Parties Help", CC.PRIMARY, true));
				player.message(new ChatMessage("/parties enable <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/parties setdisplay <DisplayName>", CC.SECONDARY, false));
				player.message(new ChatMessage("/parties slot <Slot>", CC.SECONDARY, false));

				return;
			case "enable":
				if (args.length < 2) {
					player.message(new UsageMessage("/parties enable <True/False>"));
					return;
				}

				if (args[1].equalsIgnoreCase("false")) {
					partyManager.setEnabled(false);
					player.message(new SetValueMessage("Parties", "false", CC.RED));
					return;
				}

				if (args[1].equalsIgnoreCase("true")) {
					partyManager.setEnabled(true);
					player.message(new SetValueMessage("Parties", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/parties enable <True/False>"));

				return;
			case "setdisplay":
				if (args.length < 1) {
					player.message(new UsageMessage("/parties setdisplay <DisplayName>"));
					return;
				}

				partyManager.setDisplayItem(player.getItemInHand());

				if (args.length > 1) {
					partyManager.setDisplayName(args[1].replace("&", "§"));
				}

				player.message(new ChatMessage("The display item for parties has been set", CC.PRIMARY, false));

				return;
			case "slot":
				if (args.length < 2) {
					player.message(new UsageMessage("/parties slot <Slot>"));
					return;
				}

				int slot = Integer.parseInt(args[1]);
				partyManager.setSlot(slot);
				player.message(new ChatMessage("The slot for parties has been set to " + slot, CC.PRIMARY, false)
						.highlightText(CC.ACCENT, " " + slot));

				return;
		}
	}
}
