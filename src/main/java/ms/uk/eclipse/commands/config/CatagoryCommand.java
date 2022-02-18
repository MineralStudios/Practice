package ms.uk.eclipse.commands.config;

import java.util.Iterator;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.rank.RankPower;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.CatagoryManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.messages.ChatMessages;
import ms.uk.eclipse.util.messages.ErrorMessages;
import ms.uk.eclipse.util.messages.UsageMessages;

public class CatagoryCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();
	final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();

	public CatagoryCommand() {
		super("catagory", RankPower.MANAGER);
	}

	@Override
	public void execute(org.bukkit.entity.Player player, String[] args) {

		String arg = args.length > 0 ? args[0] : "";

		Catagory catagory;
		Gametype gametype;
		String catagoryName;
		String gametypeName;
		StringBuilder sb;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.CATAGORY_COMMANDS.send(player);
				ChatMessages.CATAGORY_CREATE.send(player);
				ChatMessages.CATAGORY_DISPLAY.send(player);
				ChatMessages.CATAGORY_QUEUE.send(player);
				ChatMessages.CATAGORY_LIST.send(player);
				ChatMessages.CATAGORY_ADD.send(player);
				ChatMessages.CATAGORY_REMOVE.send(player);
				ChatMessages.CATAGORY_DELETE.send(player);
				return;
			case "create":
				if (args.length < 2) {
					UsageMessages.CATAGORY_CREATE.send(player);
					return;
				}

				catagoryName = args[1];

				if (catagoryManager.getCatagoryByName(catagoryName) != null) {
					ErrorMessages.ARENA_ALREADY_EXISTS.send(player);
					return;
				}

				catagory = new Catagory(catagoryName);
				catagoryManager.registerCatagory(catagory);
				ChatMessages.CATAGORY_CREATED.clone().replace("%catagory%", catagoryName).send(player);
				return;
			case "setdisplay":
				if (args.length < 2) {
					UsageMessages.CATAGORY_DISPLAY.send(player);
					return;
				}

				catagoryName = args[1];
				catagory = catagoryManager.getCatagoryByName(catagoryName);

				if (catagory == null) {
					ErrorMessages.CATAGORY_DOES_NOT_EXIST.send(player);
					return;
				}

				catagory.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					catagory.setDisplayName(args[2].replace("&", "§"));
				}

				ChatMessages.CATAGORY_DISPLAY_SET.clone().replace("%catagory%", catagoryName).send(player);

				return;
			case "queue":
				if (args.length < 4) {
					UsageMessages.CATAGORY_QUEUE.send(player);
					return;
				}

				catagoryName = args[1];
				catagory = catagoryManager.getCatagoryByName(catagoryName);

				Queuetype queuetype = queuetypeManager.getQueuetypeByName(args[2]);

				if (catagory == null) {
					ErrorMessages.CATAGORY_DOES_NOT_EXIST.send(player);
					return;
				}

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				String slotName = args[3];

				if (slotName.equalsIgnoreCase("false")) {
					catagory.removeFromQueuetype(queuetype);
				} else {
					Integer slot;

					try {
						slot = Integer.parseInt(slotName);
					} catch (Exception e) {
						ErrorMessages.INVALID_SLOT.send(player);
						return;
					}

					catagory.addToQueuetype(queuetype, slot);
				}

				ChatMessages.CATAGORY_QUEUE.clone().replace("%catagory%", catagoryName).replace("%slot%",
						slotName).send(player);

				return;
			case "add":
				if (args.length < 3) {
					UsageMessages.CATAGORY_ADD.send(player);
					return;
				}

				catagoryName = args[1];
				catagory = catagoryManager.getCatagoryByName(catagoryName);

				if (catagory == null) {
					ErrorMessages.CATAGORY_DOES_NOT_EXIST.send(player);
					return;
				}

				gametypeName = args[2];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				gametype.addToCatagory(catagory);
				ChatMessages.CATAGORY_ADDED.clone().replace("%gametype%", gametypeName)
						.replace("%catagory%", catagoryName).send(player);

				return;
			case "remove":
				if (args.length < 3) {
					UsageMessages.CATAGORY_REMOVE.send(player);
					return;
				}

				catagoryName = args[1];
				catagory = catagoryManager.getCatagoryByName(catagoryName);

				if (catagory == null) {
					ErrorMessages.CATAGORY_DOES_NOT_EXIST.send(player);
					return;
				}

				gametypeName = args[2];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				gametype.removeFromCatagory(catagory);
				ChatMessages.CATAGORY_REMOVED.clone().replace("%gametype%", gametypeName)
						.replace("%catagory%", catagoryName).send(player);

				return;
			case "list":
				sb = new StringBuilder(CC.GRAY + "[");

				Iterator<Catagory> arenaIter = catagoryManager.getCatagorys().iterator();

				while (arenaIter.hasNext()) {
					Catagory c = arenaIter.next();
					sb.append(CC.GREEN + c.getName());

					if (arenaIter.hasNext()) {
						sb.append(CC.GRAY + ", ");
					}
				}

				sb.append(CC.GRAY + "]");

				player.sendMessage(sb.toString());

				return;
			case "delete":
				if (args.length < 2) {
					UsageMessages.CATAGORY_DELETE.send(player);
					return;
				}

				catagoryName = args[1];
				catagory = catagoryManager.getCatagoryByName(catagoryName);

				if (catagory == null) {
					ErrorMessages.CATAGORY_DOES_NOT_EXIST.send(player);
					return;
				}

				catagoryManager.remove(catagory);
				ChatMessages.CATAGORY_DELETED.clone().replace("%catagory%", catagoryName).send(player);

				return;
		}
	}
}
