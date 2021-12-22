package ms.uk.eclipse.commands.config;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.AddedMessage;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.CreatedMessage;
import ms.uk.eclipse.core.utils.message.DeletedMessage;
import ms.uk.eclipse.core.utils.message.SetValueMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.CatagoryManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class CatagoryCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();
	final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();

	public CatagoryCommand() {
		super("catagory", "practice.permission.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = "";

		if (args.length > 0) {
			arg = args[0];
		}

		Profile player = playerManager.getProfile(pl);
		Catagory catagory;
		Gametype gametype;

		switch (arg.toLowerCase()) {
			case "create":
				if (args.length < 2) {
					player.message(new UsageMessage("/catagory create <Name>"));
					return;
				}

				catagory = new Catagory(args[1]);

				if (catagoryManager.contains(catagory)) {
					player.message(ErrorMessages.CATAGORY_ALREADY_EXISTS);
					return;
				}

				catagoryManager.registerCatagory(catagory);

				player.message(new CreatedMessage("The " + args[1] + " catagory"));

				return;
			case "setdisplay":
				if (args.length < 2) {
					player.message(new UsageMessage("/catagory setdisplay <Catagory> <DisplayName>"));
					return;
				}

				catagory = catagoryManager.getCatagoryByName(args[1]);

				if (catagory == null) {
					player.message(ErrorMessages.CATAGORY_DOES_NOT_EXIST);
					return;
				}

				catagory.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					catagory.setDisplayName(args[2].replace("&", "§"));
				}

				player.message(
						new SetValueMessage("The display item for " + args[1], "the item in your hand", CC.GREEN));

				return;
			case "queue":
				if (args.length < 4) {
					player.message(new UsageMessage("/catagory queue <Catagory> <Queuetype> <Slot/False>"));
					return;
				}

				catagory = catagoryManager.getCatagoryByName(args[1]);

				Queuetype queuetype = queuetypeManager.getQueuetypeByName(args[2]);

				if (catagory == null) {
					player.message(ErrorMessages.CATAGORY_DOES_NOT_EXIST);
					return;
				}

				if (queuetype == null) {
					player.message(ErrorMessages.QUEUETYPE_DOES_NOT_EXIST);
					return;
				}

				if (args[3].equalsIgnoreCase("false")) {
					catagory.removeFromQueuetype(queuetype);
				} else {
					Integer slot;

					try {
						slot = Integer.parseInt(args[3]);
					} catch (Exception e) {
						return;
					}

					catagory.addToQueuetype(queuetype, slot);
				}

				player.message(new SetValueMessage("The slot in the queue for " + args[1], args[3], CC.GREEN));

				return;
			case "add":
				if (args.length < 3) {
					player.message(new UsageMessage("/catagory add <Catagory> <Gametype>"));
					return;
				}

				catagory = catagoryManager.getCatagoryByName(args[1]);
				gametype = gametypeManager.getGametypeByName(args[2]);

				if (catagory == null) {
					player.message(ErrorMessages.CATAGORY_DOES_NOT_EXIST);
					return;
				}

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				gametype.addToCatagory(catagory);
				player.message(new AddedMessage(args[2], "the catagory", CC.GREEN));

				return;
			case "remove":
				if (args.length < 3) {
					player.message(new UsageMessage("/catagory remove <Catagory> <Gametype>"));
					return;
				}

				catagory = catagoryManager.getCatagoryByName(args[1]);
				gametype = gametypeManager.getGametypeByName(args[2]);

				if (catagory == null) {
					player.message(ErrorMessages.CATAGORY_DOES_NOT_EXIST);
					return;
				}

				if (gametype == null) {
					player.message(ErrorMessages.GAMETYPE_DOES_NOT_EXIST);
					return;
				}

				gametype.removeFromCatagory(catagory);
				player.message(new DeletedMessage(args[2]));

				return;
			case "list":
				player.message(new StrikingMessage("Catagory List", CC.PRIMARY, true));

				for (Catagory c : catagoryManager.getCatagorys()) {
					player.message(new ChatMessage(c.getName(), CC.SECONDARY, false));
				}

				return;
			case "delete":
				if (args.length < 2) {
					player.message(new UsageMessage("/catagory delete <Catagory>"));
					return;
				}

				catagory = catagoryManager.getCatagoryByName(args[1]);

				if (catagory == null) {
					player.message(ErrorMessages.CATAGORY_DOES_NOT_EXIST);
					return;
				}

				catagoryManager.remove(catagory);
				player.message(new DeletedMessage("The " + args[1] + " catagory"));

				return;
			default:
				player.message(new StrikingMessage("Catagory Help", CC.PRIMARY, true));
				player.message(new ChatMessage("/catagory create <Name>", CC.SECONDARY, false));
				player.message(new ChatMessage("/catagory setdisplay <Catagory> <DisplayName>", CC.SECONDARY, false));
				player.message(
						new ChatMessage("/catagory queue <Catagory> <Queuetype> <Slot/False>", CC.SECONDARY, false));
				player.message(new ChatMessage("/catagory list", CC.SECONDARY, false));
				player.message(new ChatMessage("/catagory add <Catagory> <Gametype>", CC.SECONDARY, false));
				player.message(new ChatMessage("/catagory remove <Catagory> <Gametype>", CC.SECONDARY, false));
				player.message(new ChatMessage("/catagory delete <Name>", CC.SECONDARY, false));

				return;
		}
	}
}
