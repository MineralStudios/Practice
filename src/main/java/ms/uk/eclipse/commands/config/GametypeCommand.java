package ms.uk.eclipse.commands.config;

import java.util.Iterator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.rank.RankPower;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.kit.Kit;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.messages.ChatMessages;
import ms.uk.eclipse.util.messages.ErrorMessages;
import ms.uk.eclipse.util.messages.UsageMessages;

public class GametypeCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();

	public GametypeCommand() {
		super("gametype", RankPower.MANAGER);
	}

	@Override
	public void execute(Player player, String[] args) {

		String arg = args.length > 0 ? args[0] : "";

		Gametype gametype;
		Arena arena;
		String gametypeName;
		String toggled;
		String arenaName;
		StringBuilder sb;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.GAMETYPE_COMMANDS.send(player);
				ChatMessages.GAMETYPE_CREATE.send(player);
				ChatMessages.GAMETYPE_KIT.send(player);
				ChatMessages.GAMETYPE_LOAD_KIT.send(player);
				ChatMessages.GAMETYPE_DAMAGE_TICKS.send(player);
				ChatMessages.GAMETYPE_GRIEFING.send(player);
				ChatMessages.GAMETYPE_BUILD.send(player);
				ChatMessages.GAMETYPE_LOOTING.send(player);
				ChatMessages.GAMETYPE_DAMAGE.send(player);
				ChatMessages.GAMETYPE_HUNGER.send(player);
				ChatMessages.GAMETYPE_BOXING.send(player);
				ChatMessages.GAMETYPE_REGEN.send(player);
				ChatMessages.GAMETYPE_EPEARL.send(player);
				ChatMessages.GAMETYPE_ARENA_FOR_ALL.send(player);
				ChatMessages.GAMETYPE_DISPLAY.send(player);
				ChatMessages.GAMETYPE_QUEUE.send(player);
				ChatMessages.GAMETYPE_LIST.send(player);
				ChatMessages.GAMETYPE_ARENA.send(player);
				ChatMessages.GAMETYPE_EVENT_ARENA.send(player);
				ChatMessages.GAMETYPE_EVENT.send(player);
				ChatMessages.GAMETYPE_DEADLY_WATER.send(player);
				ChatMessages.GAMETYPE_DELETE.send(player);
				return;
			case "create":
				if (args.length < 2) {
					UsageMessages.GAMETYPE_CREATE.send(player);
					return;
				}

				gametypeName = args[1];

				if (gametypeManager.getGametypeByName(gametypeName) != null) {
					ErrorMessages.GAMETYPE_ALREADY_EXISTS.send(player);
					return;
				}

				gametype = new Gametype(gametypeName);
				gametype.setDefaults();
				gametypeManager.registerGametype(gametype);
				ChatMessages.GAMETYPE_CREATED.clone().replace("%gametype%", gametypeName).send(player);
				return;
			case "loadkit":
				if (args.length < 2) {
					UsageMessages.GAMETYPE_LOAD_KIT.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				player.getInventory().setContents(gametype.getKit().getContents());
				player.getInventory().setArmorContents(gametype.getKit().getArmourContents());
				ChatMessages.GAMETYPE_LOADED_KIT.clone().replace("%gametype%", gametypeName).send(player);

				return;
			case "kit":
				if (args.length < 2) {
					UsageMessages.GAMETYPE_KIT.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				ItemStack[] contents = player.getInventory().getContents();
				ItemStack[] armourContents = player.getInventory().getArmorContents();

				gametype.setKit(new Kit(contents, armourContents));
				ChatMessages.GAMETYPE_KIT_SET.clone().replace("%gametype%", gametypeName).send(player);

				return;
			case "setdisplay":
				if (args.length < 2) {
					UsageMessages.GAMETYPE_DISPLAY.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				gametype.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					gametype.setDisplayName(args[2].replace("&", "§"));
				}

				ChatMessages.GAMETYPE_DISPLAY_SET.clone().replace("%gametype%", gametypeName).send(player);
				return;
			case "hitdelay":
			case "nodamageticks":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_DAMAGE_TICKS.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				String noDamageTicksStr = args[2];

				Integer ndt;

				try {
					ndt = Integer.parseInt(noDamageTicksStr);
				} catch (Exception e) {
					ErrorMessages.INVALID_NUMBER.send(player);
					return;
				}

				gametype.setNoDamageTicks(ndt);
				ChatMessages.GAMETYPE_DAMAGE_TICKS_SET.clone().replace("%gametype%", gametypeName).replace("%delay%",
						noDamageTicksStr).send(player);
				return;
			case "regen":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_REGEN.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setRegeneration(false);
						break;
					case "true":
						gametype.setRegeneration(true);
						break;
					default:
						UsageMessages.GAMETYPE_REGEN.send(player);
						return;
				}

				ChatMessages.GAMETYPE_REGEN_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);
				return;
			case "griefing":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_GRIEFING.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setGriefing(false);
						break;
					case "true":
						gametype.setGriefing(true);
						break;
					default:
						UsageMessages.GAMETYPE_GRIEFING.send(player);
						return;
				}

				ChatMessages.GAMETYPE_GRIEFING_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);

				return;
			case "queue":
				if (args.length < 4) {
					UsageMessages.GAMETYPE_QUEUE.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				String queuetypeName = args[2];
				Queuetype queuetype = queuetypeManager.getQueuetypeByName(queuetypeName);

				if (queuetype == null) {
					ErrorMessages.QUEUETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				String slotName = args[3];

				if (slotName.equalsIgnoreCase("false")) {
					gametype.removeFromQueuetype(queuetype);
				} else {
					Integer slot;

					try {
						slot = Integer.parseInt(slotName);
					} catch (Exception e) {
						ErrorMessages.INVALID_SLOT.send(player);
						return;
					}

					gametype.addToQueuetype(queuetype, slot);
				}

				ChatMessages.GAMETYPE_SLOT_SET.clone().replace("%gametype%", gametypeName)
						.replace("%queuetype%", queuetypeName).replace("%slot%", slotName).send(player);
				return;
			case "build":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_BUILD.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setBuild(false);
						break;
					case "true":
						gametype.setBuild(true);
						break;
					default:
						UsageMessages.GAMETYPE_BUILD.send(player);
						return;
				}

				ChatMessages.GAMETYPE_BUILD_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);

				return;
			case "deadlywater":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_DEADLY_WATER.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setDeadlyWater(false);
						break;
					case "true":
						gametype.setDeadlyWater(true);
						break;
					default:
						UsageMessages.GAMETYPE_DEADLY_WATER.send(player);
						return;
				}

				ChatMessages.GAMETYPE_DEADLY_WATER_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);

				return;
			case "looting":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_LOOTING.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setLooting(false);
						break;
					case "true":
						gametype.setLooting(true);
						break;
					default:
						UsageMessages.GAMETYPE_LOOTING.send(player);
						return;
				}

				ChatMessages.GAMETYPE_LOOTING_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);

				return;
			case "damage":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_DAMAGE.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setDamage(false);
						break;
					case "true":
						gametype.setDamage(true);
						break;
					default:
						UsageMessages.GAMETYPE_DAMAGE.send(player);
						return;
				}

				ChatMessages.GAMETYPE_DAMAGE_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);

				return;
			case "hunger":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_HUNGER.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setHunger(false);
						break;
					case "true":
						gametype.setHunger(true);
						break;
					default:
						UsageMessages.GAMETYPE_HUNGER.send(player);
						return;
				}

				ChatMessages.GAMETYPE_HUNGER_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);

				return;
			case "boxing":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_BOXING.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setBoxing(false);
						break;
					case "true":
						gametype.setBoxing(true);
						break;
					default:
						UsageMessages.GAMETYPE_BOXING.send(player);
						return;
				}

				ChatMessages.GAMETYPE_BOXING_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);

				return;
			case "epearl":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_EPEARL.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				String cooldownStr = args[2];

				Integer number;

				try {
					number = Integer.parseInt(cooldownStr);
				} catch (Exception e) {
					ErrorMessages.INVALID_NUMBER.send(player);
					return;
				}

				gametype.setPearlCooldown(number);
				ChatMessages.GAMETYPE_PEARL_COOLDOWN_SET.clone().replace("%gametype%", gametypeName)
						.replace("%cooldown%", cooldownStr).send(player);
				return;
			case "list":
				sb = new StringBuilder(CC.GRAY + "[");

				Iterator<Gametype> gametypeIter = gametypeManager.getGametypes().iterator();

				while (gametypeIter.hasNext()) {
					Gametype g = gametypeIter.next();
					sb.append(CC.GREEN + g.getName());

					if (gametypeIter.hasNext()) {
						sb.append(CC.GRAY + ", ");
					}
				}

				sb.append(CC.GRAY + "]");

				player.sendMessage(sb.toString());
				return;
			case "arena":
				if (args.length < 4) {
					UsageMessages.GAMETYPE_ARENA.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				arenaName = args[2];
				arena = arenaManager.getArenaByName(arenaName);

				if (arena == null) {
					ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[3].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.enableArena(arena, false);
						break;
					case "true":
						gametype.enableArena(arena, true);
						break;
					default:
						UsageMessages.GAMETYPE_ARENA.send(player);
						return;
				}

				ChatMessages.GAMETYPE_ARENA_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).replace("%arena%", arenaName).send(player);

				return;
			case "event":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_EVENT.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						gametype.setEvent(false);
						break;
					case "true":
						gametype.setEvent(true);
						break;
					default:
						UsageMessages.GAMETYPE_EVENT.send(player);
						return;
				}

				ChatMessages.GAMETYPE_EVENT_SET.clone().replace("%gametype%", gametypeName)
						.replace("%toggled%", toggled).send(player);

				return;
			case "seteventarena":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_EVENT_ARENA.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				arenaName = args[2];
				arena = arenaManager.getArenaByName(arenaName);

				if (arena == null) {
					ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
					return;
				}

				gametype.setEventArena(arena);
				ChatMessages.GAMETYPE_EVENT_ARENA_SET.clone().replace("%gametype%", gametypeName)
						.replace("%arena%", arenaName).send(player);
				return;

			case "enablearenaforall":
				if (args.length < 3) {
					UsageMessages.GAMETYPE_ARENA_FOR_ALL.send(player);
					return;
				}

				arenaName = args[1];
				arena = arenaManager.getArenaByName(arenaName);

				if (arena == null) {
					ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
					return;
				}

				toggled = args[2].toLowerCase();

				switch (toggled) {
					case "false":
						for (Gametype g : gametypeManager.getGametypes()) {
							g.enableArena(arena, false);
						}
						break;
					case "true":
						for (Gametype g : gametypeManager.getGametypes()) {
							g.enableArena(arena, true);
						}
						break;
					default:
						UsageMessages.GAMETYPE_ARENA_FOR_ALL.send(player);
						return;
				}

				ChatMessages.GAMETYPE_ARENA_FOR_ALL_SET.clone().replace("%arena%", arenaName)
						.replace("%toggled%", toggled).send(player);
				return;
			case "delete":
				if (args.length < 2) {
					UsageMessages.GAMETYPE_DELETE.send(player);
					return;
				}

				gametypeName = args[1];
				gametype = gametypeManager.getGametypeByName(gametypeName);

				if (gametype == null) {
					ErrorMessages.GAMETYPE_DOES_NOT_EXIST.send(player);
					return;
				}

				gametypeManager.remove(gametype);
				ChatMessages.GAMETYPE_DELETED.clone().replace("%gametype%", gametypeName).send(player);

				return;
		}
	}
}
