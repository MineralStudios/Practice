package ms.uk.eclipse.commands.config;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.rank.RankPower;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.PlayerUtil;
import ms.uk.eclipse.util.messages.ChatMessages;
import ms.uk.eclipse.util.messages.ErrorMessages;
import ms.uk.eclipse.util.messages.UsageMessages;

public class ArenaCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();

	public ArenaCommand() {
		super("arena", RankPower.MANAGER);
	}

	@Override
	public void execute(Player player, String[] args) {

		String arg = args.length > 0 ? args[0] : "";
		Arena arena;
		String arenaName;
		StringBuilder sb;

		switch (arg.toLowerCase()) {
			default:
				ChatMessages.ARENA_COMMANDS.send(player);
				ChatMessages.ARENA_CREATE.send(player);
				ChatMessages.ARENA_SPAWN.send(player);
				ChatMessages.ARENA_DISPLAY.send(player);
				ChatMessages.ARENA_LIST.send(player);
				ChatMessages.ARENA_TP.send(player);
				ChatMessages.ARENA_DELETE.send(player);
				return;
			case "create":
				if (args.length < 2) {
					UsageMessages.ARENA_CREATE.send(player);
					return;
				}

				arenaName = args[1];

				if (arenaManager.getArenaByName(arenaName) != null) {
					ErrorMessages.ARENA_ALREADY_EXISTS.send(player);
					return;
				}

				arena = new Arena(arenaName);
				arena.setDefaults();
				arenaManager.registerArena(arena);
				ChatMessages.ARENA_CREATED.clone().replace("%arena%", arenaName).send(player);
				return;
			case "spawn":
				if (args.length < 3) {
					UsageMessages.ARENA_SPAWN.send(player);
					return;
				}

				arenaName = args[1];
				arena = arenaManager.getArenaByName(arenaName);

				if (arena == null) {
					ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
					return;
				}

				Location loc = player.getLocation();

				switch (args[2].toLowerCase()) {
					case "1":
						arena.setLocation1(loc);
						arena.setLocation1EyeVector(loc.getDirection());
						break;
					case "2":
						arena.setLocation2(loc);
						arena.setLocation2EyeVector(loc.getDirection());
						break;
					case "waiting":
						arena.setWaitingLocation(loc);
						break;
					default:
						UsageMessages.ARENA_SPAWN.send(player);
						return;
				}

				ChatMessages.ARENA_SPAWN_SET.clone().replace("%arena%", arenaName).send(player);
				return;
			case "setdisplay":
				if (args.length < 2) {
					UsageMessages.ARENA_DISPLAY.send(player);
					return;
				}

				arenaName = args[1];
				arena = arenaManager.getArenaByName(arenaName);

				if (arena == null) {
					ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
					return;
				}

				arena.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					arena.setDisplayName(args[2].replace("&", "§"));
				}

				ChatMessages.ARENA_DISPLAY_SET.clone().replace("%arena%", arenaName).send(player);
				return;
			case "list":
				sb = new StringBuilder(CC.GRAY + "[");

				Iterator<Arena> arenaIter = arenaManager.getArenas().iterator();

				while (arenaIter.hasNext()) {
					Arena a = arenaIter.next();
					sb.append(CC.GREEN + a.getName());

					if (arenaIter.hasNext()) {
						sb.append(CC.GRAY + ", ");
					}
				}

				sb.append(CC.GRAY + "]");

				player.sendMessage(sb.toString());
				return;
			case "teleport":
			case "tp":
				if (args.length < 2) {
					UsageMessages.ARENA_TP.send(player);
					return;
				}

				arena = arenaManager.getArenaByName(args[1]);

				if (arena == null) {
					ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
					return;
				}

				try {
					PlayerUtil.teleport((CraftPlayer) player, arena.getLocation1());
				} catch (Exception e) {
					ErrorMessages.CANNOT_TELEPORT_TO_ARENA.send(player);
					e.printStackTrace();
				}

				return;
			case "remove":
			case "delete":
				if (args.length < 2) {
					UsageMessages.ARENA_DELETE.send(player);
					return;
				}

				arenaName = args[1];
				arena = arenaManager.getArenaByName(args[1]);

				if (arena == null) {
					ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
					return;
				}

				arenaManager.remove(arena);
				ChatMessages.ARENA_DELETED.clone().replace("%arena%", arenaName).send(player);

				return;
		}
	}
}
