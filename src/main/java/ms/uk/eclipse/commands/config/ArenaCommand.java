package ms.uk.eclipse.commands.config;

import org.bukkit.Location;

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
import ms.uk.eclipse.util.messages.ErrorMessages;

public class ArenaCommand extends PlayerCommand {

	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();

	public ArenaCommand() {
		super("arena", "practice.permission.config");
	}

	@Override
	public void execute(org.bukkit.entity.Player pl, String[] args) {

		String arg = "";

		if (args.length > 0) {
			arg = args[0];
		}

		Profile player = playerManager.getProfile(pl);
		Arena arena;

		switch (arg.toLowerCase()) {
			default:
				player.message(new StrikingMessage("Arena Help", CC.PRIMARY, true));
				player.message(new ChatMessage("/arena create <Name>", CC.SECONDARY, false));
				player.message(new ChatMessage("/arena spawn <Arena> <1/2/Waiting>", CC.SECONDARY, false));
				player.message(new ChatMessage("/arena setdisplay <Arena> <&{Colour}>", CC.SECONDARY, false));
				player.message(new ChatMessage("/arena list", CC.SECONDARY, false));
				player.message(new ChatMessage("/arena tp <Arena>", CC.SECONDARY, false));
				player.message(new ChatMessage("/arena waitinglocation <Arena>", CC.SECONDARY, false));
				player.message(new ChatMessage("/arena delete <Name>", CC.SECONDARY, false));

				return;
			case "create":
				if (args.length < 2) {
					player.message(new UsageMessage("/arena create <Name>"));
					return;
				}

				arena = new Arena(args[1]);

				if (arenaManager.contains(arena)) {
					player.message(ErrorMessages.ARENA_ALREADY_EXISTS);
					return;
				}

				arenaManager.registerArena(arena);
				player.message(new CreatedMessage("The " + args[1] + " arena"));

				return;
			case "spawn":
				if (args.length < 3) {
					player.message(new UsageMessage("/arena spawn <Arena> <1/2>"));
					return;
				}

				arena = arenaManager.getArenaByName(args[1]);

				if (arena == null) {
					player.message(ErrorMessages.ARENA_DOES_NOT_EXIST);
					return;
				}

				Location loc = player.bukkit().getLocation();
				SetValueMessage m = new SetValueMessage("The spawn location for " + args[2], "your location", CC.GREEN);

				switch (args[2].toLowerCase()) {
					case "1":
						arena.setLocation1(loc);
						arena.setLocation1EyeVector(loc.getDirection());
						player.message(m);
						break;
					case "2":
						arena.setLocation2(loc);
						arena.setLocation2EyeVector(loc.getDirection());
						player.message(m);
						break;
					case "waiting":
						arena.setWaitingLocation(loc);
						player.message(m);
						break;
					default:
						player.message(new UsageMessage("/arena spawn <Arena> <1/2>"));
				}

				return;
			case "setdisplay":
				if (args.length < 2) {
					player.message(new UsageMessage("/arena setdisplay <Arena> <DisplayName>"));
					return;
				}

				arena = arenaManager.getArenaByName(args[1]);

				if (arena == null) {
					player.message(ErrorMessages.ARENA_DOES_NOT_EXIST);
					return;
				}

				arena.setDisplayItem(player.getItemInHand());

				if (args.length > 2) {
					arena.setDisplayName(args[2].replace("&", "§"));
				}

				player.message(
						new SetValueMessage("The display item for " + args[1], "the item in your hand", CC.GREEN));

				return;
			case "list":
				player.message(new StrikingMessage("Arena List", CC.PRIMARY, true));

				for (Arena a : arenaManager.getArenas()) {
					player.message(new ChatMessage(a.getName(), CC.SECONDARY, false));
				}

				return;
			case "tp":
				if (args.length < 2) {
					player.message(new UsageMessage("/arena tp <Arena>"));
					return;
				}

				arena = arenaManager.getArenaByName(args[1]);

				if (arena == null) {
					player.message(ErrorMessages.ARENA_DOES_NOT_EXIST);
					return;
				}

				try {
					player.teleport(arena.getLocation1());
				} catch (Exception e) {
					player.message(ErrorMessages.CANNOT_TELEPORT_TO_ARENA);
				}

				return;
			case "waitinglocation":
				if (args.length < 2) {
					player.message(new UsageMessage("/arena waitinglocation <Arena>"));
					return;
				}

				arena = arenaManager.getArenaByName(args[1]);

				if (arena == null) {
					player.message(ErrorMessages.ARENA_DOES_NOT_EXIST);
					return;
				}

				arena.setWaitingLocation(player.bukkit().getLocation());

				player.message(new SetValueMessage("The waiting location for " + args[1], "your location", CC.GREEN));

				return;
			case "delete":
				if (args.length < 2) {
					player.message(new UsageMessage("/arena delete <Arena>"));
					return;
				}

				arena = arenaManager.getArenaByName(args[1]);

				if (arena == null) {
					player.message(ErrorMessages.ARENA_DOES_NOT_EXIST);
					return;
				}

				arenaManager.remove(arena);
				player.message(new DeletedMessage("The " + args[1] + " arena"));

				return;
		}
	}
}
