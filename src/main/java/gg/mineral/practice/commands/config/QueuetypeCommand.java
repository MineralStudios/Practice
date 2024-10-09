package gg.mineral.practice.commands.config;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.menus.QueuetypeArenaEnableMenu;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;

import it.unimi.dsi.fastutil.objects.ObjectIterator;

public class QueuetypeCommand extends PlayerCommand {

	public QueuetypeCommand() {
		super("queuetype", "practice.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player player, String[] args) {

		String arg = args.length > 0 ? args[0] : "";

		Queuetype queuetype;
		String queuetypeName;
		String toggled;
		StringBuilder sb;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.QUEUETYPE_COMMANDS.send(player);
				ChatMessages.QUEUETYPE_CREATE.send(player);
				ChatMessages.QUEUETYPE_DISPLAY.send(player);
				ChatMessages.QUEUETYPE_RANKED.send(player);
				ChatMessages.QUEUETYPE_COMMUNITY.send(player);
				ChatMessages.QUEUETYPE_UNRANKED.send(player);
				ChatMessages.QUEUETYPE_BOTS.send(player);
				ChatMessages.QUEUETYPE_SLOT.send(player);
				ChatMessages.QUEUETYPE_KB.send(player);
				ChatMessages.QUEUETYPE_LIST.send(player);
				ChatMessages.QUEUETYPE_ARENA.send(player);
				ChatMessages.QUEUETYPE_DELETE.send(player);
				return;
			case "create":
				if (args.length < 2) {
					UsageMessages.QUEUETYPE_CREATE.send(player);
					return;
				}

				queuetypeName = args[1];

				if (QueuetypeManager.getQueuetypeByName(queuetypeName) != null) {
					ErrorMessages.QUEUETYPE_ALREADY_EXISTS.send(player);
					return;
				}

				queuetype = new Queuetype(queuetypeName, QueuetypeManager.CURRENT_ID++);
				queuetype.setDefaults();
				QueuetypeManager.registerQueuetype(queuetype);
				ChatMessages.QUEUETYPE_CREATED.clone().replace("%queuetype%", queuetypeName).send(player);

				return;
			case "setdisplay":
				if (args.length < 2) {
					UsageMessages.QUEUETYPE_DISPLAY.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				queuetype.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					queuetype.setDisplayName(args[2].replace("&", "§"));
				}

				ChatMessages.QUEUETYPE_DISPLAY_SET.clone().replace("%queuetype%", queuetypeName).send(player);

				return;
			case "ranked":
			case "elo":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_RANKED.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						queuetype.setRanked(false);
						break;
					case "true":
						queuetype.setRanked(true);
						break;
					default:
						UsageMessages.QUEUETYPE_RANKED.send(player);
						return;
				}

				ChatMessages.QUEUETYPE_RANKED_SET.clone().replace("%queuetype%", queuetypeName)
						.replace("%toggled%", toggled).send(player);
				return;
			case "community":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_COMMUNITY.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						queuetype.setCommunity(false);
						break;
					case "true":
						queuetype.setCommunity(true);
						break;
					default:
						UsageMessages.QUEUETYPE_COMMUNITY.send(player);
						return;
				}

				ChatMessages.QUEUETYPE_COMMUNITY_SET.clone().replace("%queuetype%", queuetypeName)
						.replace("%toggled%", toggled).send(player);
				return;
			case "unranked":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_UNRANKED.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						queuetype.setUnranked(false);
						break;
					case "true":
						queuetype.setUnranked(true);
						break;
					default:
						UsageMessages.QUEUETYPE_UNRANKED.send(player);
						return;
				}

				ChatMessages.QUEUETYPE_UNRANKED_SET.clone().replace("%queuetype%", queuetypeName)
						.replace("%toggled%", toggled).send(player);
				return;
			case "bots":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_BOTS.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						queuetype.setBotsEnabled(false);
						break;
					case "true":
						queuetype.setBotsEnabled(true);
						break;
					default:
						UsageMessages.QUEUETYPE_BOTS.send(player);
						return;
				}

				ChatMessages.QUEUETYPE_BOTS_SET.clone().replace("%queuetype%", queuetypeName)
						.replace("%toggled%", toggled).send(player);
				return;
			case "slot":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_SLOT.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				int slot;
				String slotName = args[2];

				try {
					slot = Integer.parseInt(slotName);
				} catch (Exception e) {
					ErrorMessages.INVALID_SLOT.send(player);
					return;
				}

				queuetype.setSlotNumber(slot);
				ChatMessages.QUEUETYPE_SLOT_SET.clone().replace("%queuetype%", queuetypeName).replace("%slot%",
						slotName).send(player);

				return;
			case "kb":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_KB.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				String knockbackName = args[2];
				KnockbackProfile kb = KnockbackProfileList.getKnockbackProfileByName(knockbackName);

				if (kb == null) {
					ErrorMessages.KNOCKBACK_DOES_NOT_EXIST.send(player);
					return;
				}

				queuetype.setKnockback(kb);
				ChatMessages.QUEUETYPE_KB_SET.clone().replace("%queuetype%", queuetypeName)
						.replace("%knockback%", knockbackName).send(player);

				return;
			case "list":
				sb = new StringBuilder(CC.GRAY + "[");

				ObjectIterator<Queuetype> queuetypes = QueuetypeManager.getQueuetypes().values().iterator();

				while (queuetypes.hasNext()) {
					Queuetype q = queuetypes.next();
					sb.append(CC.GREEN + q.getName());
					if (queuetypes.hasNext())
						sb.append(CC.GRAY + ", ");
				}

				sb.append(CC.GRAY + "]");

				player.sendMessage(sb.toString());

				return;
			case "arena":
				if (args.length < 2) {
					UsageMessages.QUEUETYPE_ARENA.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				Profile profile = ProfileManager.getOrCreateProfile(player);

				profile.openMenu(new QueuetypeArenaEnableMenu(queuetype));

				return;
			case "delete":
				if (args.length < 2) {
					UsageMessages.QUEUETYPE_DELETE.send(player);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				QueuetypeManager.remove(queuetype);
				ChatMessages.QUEUETYPE_DELETED.clone().replace("%queuetype%", queuetypeName).send(player);

				return;
		}
	}
}
