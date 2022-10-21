package gg.mineral.practice.commands.config;

import java.util.Iterator;

import gg.mineral.practice.commands.PlayerCommand;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;

public class QueuetypeCommand extends PlayerCommand {

	public QueuetypeCommand() {
		super("queuetype", "practice.permission.admin");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = args.length > 0 ? args[0] : "";

		Queuetype queuetype;
		String queuetypeName;
		String toggled;
		StringBuilder sb;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.QUEUETYPE_COMMANDS.send(pl);
				ChatMessages.QUEUETYPE_CREATE.send(pl);
				ChatMessages.QUEUETYPE_DISPLAY.send(pl);
				ChatMessages.QUEUETYPE_RANKED.send(pl);
				ChatMessages.QUEUETYPE_SLOT.send(pl);
				ChatMessages.QUEUETYPE_KB.send(pl);
				ChatMessages.QUEUETYPE_LIST.send(pl);
				ChatMessages.QUEUETYPE_ARENA.send(pl);
				ChatMessages.QUEUETYPE_DELETE.send(pl);
				return;
			case "create":
				if (args.length < 2) {
					UsageMessages.QUEUETYPE_CREATE.send(pl);
					return;
				}

				queuetypeName = args[1];

				if (QueuetypeManager.getByName(queuetypeName) != null) {
					ErrorMessages.QUEUETYPE_ALREADY_EXISTS.send(pl);
					return;
				}

				queuetype = new Queuetype(queuetypeName);
				queuetype.setDefaults();
				QueuetypeManager.register(queuetype);
				ChatMessages.QUEUETYPE_CREATED.clone().replace("%queuetype%", queuetypeName).send(pl);

				return;
			case "setdisplay":
				if (args.length < 2) {
					UsageMessages.QUEUETYPE_DISPLAY.send(pl);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(pl);
					return;
				}

				queuetype.setDisplayItem(pl.getItemInHand());

				if (args.length > 2) {
					queuetype.setDisplayName(args[2].replace("&", "§"));
				}

				ChatMessages.QUEUETYPE_DISPLAY_SET.clone().replace("%queuetype%", queuetypeName).send(pl);

				return;
			case "ranked":
			case "elo":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_RANKED.send(pl);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(pl);
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
						UsageMessages.QUEUETYPE_RANKED.send(pl);
						return;
				}

				ChatMessages.QUEUETYPE_RANKED_SET.clone().replace("%toggled%", toggled).send(pl);
				return;
			case "slot":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_SLOT.send(pl);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(pl);
					return;
				}

				Integer slot;
				String slotName = args[2];

				try {
					slot = Integer.parseInt(slotName);
				} catch (Exception e) {
					ErrorMessages.INVALID_SLOT.send(pl);
					return;
				}

				queuetype.setSlotNumber(slot);
				ChatMessages.QUEUETYPE_SLOT_SET.clone().replace("%queuetype%", queuetypeName).replace("%slot%",
						slotName).send(pl);

				return;
			case "kb":
				if (args.length < 3) {
					UsageMessages.QUEUETYPE_KB.send(pl);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(pl);
					return;
				}

				String knockbackName = args[2];
				KnockbackProfile kb = KnockbackProfileList.getKnockbackProfileByName(knockbackName);

				if (kb == null) {
					ErrorMessages.KNOCKBACK_DOES_NOT_EXIST.send(pl);
					return;
				}

				queuetype.setKnockback(kb);
				ChatMessages.QUEUETYPE_KB_SET.clone().replace("%queuetype%", queuetypeName)
						.replace("%knockback%", knockbackName).send(pl);

				return;
			case "list":
				sb = new StringBuilder(CC.GRAY + "[");

				Iterator<Queuetype> queuetypeIter = QueuetypeManager.list().iterator();

				while (queuetypeIter.hasNext()) {
					Queuetype q = queuetypeIter.next();
					sb.append(CC.GREEN + q.getName());

					if (queuetypeIter.hasNext()) {
						sb.append(CC.GRAY + ", ");
					}
				}

				sb.append(CC.GRAY + "]");

				pl.sendMessage(sb.toString());

				return;
			case "arena":
				if (args.length < 4) {
					UsageMessages.QUEUETYPE_ARENA.send(pl);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(pl);
					return;
				}

				String arenaName = args[2];
				Arena arena = ArenaManager.getByName(arenaName);

				toggled = args[3].toLowerCase();

				switch (toggled) {
					case "false":
						queuetype.enableArena(arena, false);
						break;
					case "true":
						queuetype.enableArena(arena, true);
						break;
					default:
						UsageMessages.QUEUETYPE_RANKED.send(pl);
						return;
				}

				ChatMessages.QUEUETYPE_ARENA_SET.clone().replace("%queuetype%", queuetypeName)
						.replace("%queuetype%", queuetypeName)
						.replace("%toggled%", toggled).replace("%arena%", arenaName).send(pl);

				return;
			case "delete":
				if (args.length < 2) {
					UsageMessages.QUEUETYPE_DELETE.send(pl);
					return;
				}

				queuetypeName = args[1];
				queuetype = QueuetypeManager.getByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(pl);
					return;
				}

				QueuetypeManager.remove(queuetype);
				ChatMessages.QUEUETYPE_DELETED.clone().replace("%queuetype%", queuetypeName).send(pl);

				return;
		}
	}
}
