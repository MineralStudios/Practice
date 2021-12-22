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
import ms.uk.eclipse.managers.PvPBotsManager;

public class PvPBotsCommand extends PlayerCommand {

	final PvPBotsManager pvPBotsManager = PracticePlugin.INSTANCE.getPvPBotsManager();
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

	public PvPBotsCommand() {
		super("pvpbots", "practice.permission.config");
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
				player.message(new StrikingMessage("PvP Bots", CC.PRIMARY, true));
				player.message(new ChatMessage("/pvpbots enable <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots create <Name>", CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots reach <PvPBot> <Reach[1.0-6.0]>", CC.SECONDARY, false));
				player.message(
						new ChatMessage("/pvpbots kb <PvPBot> <HorizontalKB[%]> <Vertical[%]>", CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots jitter <PvPBot> <Jitter[0-10]> <RandomMovement[0-10]>",
						CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots accuracy <PvPBot> <Accuracy[0-10]>", CC.SECONDARY, false));
				player.message(
						new ChatMessage("/pvpbots strafeaccuracy <PvPBot> <Accuracy[0-10]>", CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots agro <PvPBot> <Aggression[0-10]>", CC.SECONDARY, false));
				player.message(new ChatMessage(
						"/pvpbots strafe <PvPBot> <Circle[%]> <StraightLine[%]> <Randomized[%]> <Alternating[%]>",
						CC.SECONDARY, false));
				player.message(new ChatMessage(
						"/pvpbots tradestrafe <PvPBot> <Circle[%]> <StraightLine[%]> <Randomized[%]> <Alternating[%]>",
						CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots potspeed <PvPBot> <Speed[1-10]>", CC.SECONDARY, false));
				player.message(
						new ChatMessage("/pvpbots potstyle <PvPBot> <90Turn[%]> <180Turn[%]>", CC.SECONDARY, false));
				player.message(
						new ChatMessage("/pvpbots sprintreset <PvPBot> <WTap[%]> <STap[%]> <BlockHit[%]> <Sneak[%]>",
								CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots jump <PvPBot> <Jumping[0-10]>", CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots aimlocation <PvPBot> <Upper[%]> <Middle[%]> <Lower[%]>",
						CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots potlocation <PvPBot> <Upper[%]> <Middle[%]> <Lower[%]>",
						CC.SECONDARY, false));
				player.message(new ChatMessage(
						"/pvpbots strafedistance <PvPBot> <Circle[1.0-6.0]> <StraightLine[1.0-6.0]> <Randomized[1.0-6.0]> <Alternating[1.0-6.0]>",
						CC.SECONDARY, false));
				player.message(new ChatMessage(
						"/pvpbots cps <PvPBot> <Style[Jitter/Butterfly]> <Min[0-20]> <MinPercentage[%]> <Max[0-20]> <MaxPercentage[%]>",
						CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots clickrange <PvPBot> <Range[1.0-8.0]> <Randomization[1.0-8.0]>",
						CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots slot <Slot>", CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots setdisplay <DisplayName>", CC.SECONDARY, false));
				player.message(new ChatMessage("/pvpbots delete <PvPBot>", CC.SECONDARY, false));

				return;
			case "enable":
				if (args.length < 2) {
					player.message(new UsageMessage("/pvpbots enable <True/False>"));
					return;
				}

				if (args[1].equalsIgnoreCase("false")) {
					pvPBotsManager.setEnabled(false);
					player.message(new SetValueMessage("PvP bots", "disabled", CC.RED));
					return;
				}

				if (args[1].equalsIgnoreCase("true")) {
					pvPBotsManager.setEnabled(true);
					player.message(new SetValueMessage("PvP bots", "enabled", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/pvpbots enable <True/False>"));

				return;
			case "setdisplay":
				if (args.length < 1) {
					player.message(new UsageMessage("/pvpbots setdisplay <DisplayName>"));
					return;
				}

				pvPBotsManager.setDisplayItem(player.getItemInHand());

				if (args.length > 1) {
					pvPBotsManager.setDisplayName(args[1].replace("&", "§"));
				}

				player.message(new ChatMessage("The display item for pvp bots has been set", CC.PRIMARY, false));

				return;
			case "slot":
				if (args.length < 2) {
					player.message(new UsageMessage("/pvpbots slot <Slot>"));
					return;
				}

				pvPBotsManager.setSlot(Integer.parseInt(args[1]));
				player.message(new ChatMessage("The slot for pvp bots has been set", CC.PRIMARY, false));

				return;
		}
	}
}
