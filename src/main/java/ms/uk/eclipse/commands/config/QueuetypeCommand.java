package ms.uk.eclipse.commands.config;

import land.strafe.server.combat.KnockbackProfile;
import land.strafe.server.combat.KnockbackProfileList;
import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.CreatedMessage;
import ms.uk.eclipse.core.utils.message.DeletedMessage;
import ms.uk.eclipse.core.utils.message.SetValueMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class QueuetypeCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();

	public QueuetypeCommand() {
		super("queuetype", "practice.permission.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = "";
		if (args.length > 0) {
			arg = args[0];
		}

		Profile player = playerManager.getProfile(pl);
		Queuetype queuetype;
		switch (arg.toLowerCase()) {
			default:
				player.message(new StrikingMessage("Queuetype Help", CC.PRIMARY, true));
				player.message(new ChatMessage("/queuetype create <Name>", CC.SECONDARY, false));
				player.message(new ChatMessage("/queuetype setdisplay <Queuetype> <DisplayName>", CC.SECONDARY, false));
				player.message(new ChatMessage("/queuetype elo <Queuetype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/queuetype slot <Queuetype> <Slot>", CC.SECONDARY, false));
				player.message(new ChatMessage("/queuetype kb <Queuetype> <KnockbackProfile>", CC.SECONDARY, false));
				player.message(new ChatMessage("/queuetype kbenabled <Queuetype> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/queuetype list", CC.SECONDARY, false));
				player.message(
						new ChatMessage("/queuetype arena <Queuetype> <Arena> <True/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/queuetype delete <Name>", CC.SECONDARY, false));

				return;
			case "create":
				if (args.length < 2) {
					player.message(new UsageMessage("/queuetype create <Name>"));
					return;
				}

				queuetype = new Queuetype(args[1]);

				if (queuetypeManager.contains(queuetype)) {
					player.message(ErrorMessages.QUEUETYPE_ALREADY_EXISTS);
					return;
				}

				queuetypeManager.registerQueuetype(queuetype);
				player.message(new CreatedMessage("The " + args[1] + " queue"));

				return;
			case "setdisplay":
				if (args.length < 2) {
					player.message(new UsageMessage("/queuetype setdisplay <Queuetype> <DisplayName>"));
					return;
				}

				queuetype = queuetypeManager.getQueuetypeByName(args[1]);

				if (queuetype == null) {
					player.message(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST);
					return;
				}

				queuetype.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					queuetype.setDisplayName(args[2].replace("&", "§"));
				}

				player.message(new ChatMessage("The " + args[1] + " display item has been set", CC.PRIMARY, false)
						.highlightText(CC.ACCENT, args[1]));

				return;
			case "elo":
				if (args.length < 3) {
					player.message(new UsageMessage("/queuetype ranked <Queuetype> <True/False>"));
					return;
				}

				queuetype = queuetypeManager.getQueuetypeByName(args[1]);

				if (queuetype == null) {
					player.message(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[2].equalsIgnoreCase("false")) {
					queuetype.setRanked(false);
					player.message(new SetValueMessage("Ranked", "false", CC.RED));
					return;
				}

				if (args[2].equalsIgnoreCase("true")) {
					queuetype.setRanked(true);
					player.message(new SetValueMessage("Ranked", "true", CC.GREEN));
					return;
				}

				player.message(new UsageMessage("/queuetype ranked <Queuetype> <True/False>"));

				return;
			case "slot":
				if (args.length < 3) {
					player.message(new UsageMessage("/queuetype slot <Queuetype> <Slot>"));
					return;
				}

				queuetype = queuetypeManager.getQueuetypeByName(args[1]);

				if (queuetype == null) {
					player.message(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST);
					return;
				}

				queuetype.setSlotNumber(Integer.parseInt(args[2]));
				player.message(new ChatMessage("The hotbar slot has been set", CC.PRIMARY, false));

				return;
			case "kb":
				if (args.length < 3) {
					player.message(new UsageMessage("/queuetype kb <Queuetype> <KnockbackProfile>"));
					return;
				}

				queuetype = queuetypeManager.getQueuetypeByName(args[1]);
				KnockbackProfile kb = KnockbackProfileList.getKnockbackProfileByName(args[2]);

				if (queuetype == null) {
					player.message(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST);
					return;
				}

				if (kb == null) {
					player.message(ErrorMessages.KNOCKBACK_DOES_NOT_EXIST);
					return;
				}

				queuetype.setKnockback(kb);
				player.message(new SetValueMessage("The knockback", kb.getName(), CC.GREEN));

				return;
			case "list":
				player.message(new StrikingMessage("Queuetype List", CC.PRIMARY, true));
				for (Queuetype q : queuetypeManager.getQueuetypes()) {
					player.message(new ChatMessage(q.getName(), CC.SECONDARY, false));
				}

				return;
			case "arena":
				if (args.length < 4) {
					player.message(new UsageMessage("/queuetype arena <Queuetype> <Arena> <True/False>"));
					return;
				}

				queuetype = queuetypeManager.getQueuetypeByName(args[1]);
				Arena arena = arenaManager.getArenaByName(args[2]);

				if (queuetype == null) {
					player.message(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[3].equalsIgnoreCase("false")) {
					queuetype.enableArena(arena, false);
					player.message(new SetValueMessage("That arena", "false", CC.RED));
					return;
				}

				if (args[3].equalsIgnoreCase("true")) {
					queuetype.enableArena(arena, true);
					player.message(new SetValueMessage("That arena", "true", CC.GREEN));
					return;
				}

				return;
			case "delete":
				if (args.length < 2) {
					player.message(new UsageMessage("/queuetype delete <Queuetype>"));
					return;
				}

				queuetype = queuetypeManager.getQueuetypeByName(args[1]);

				if (queuetype == null) {
					player.message(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST);
					return;
				}

				queuetypeManager.remove(queuetype);
				player.message(new DeletedMessage("The " + args[1] + " queue"));

				return;
		}
	}
}
