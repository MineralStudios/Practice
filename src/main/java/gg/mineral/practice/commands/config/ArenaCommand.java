package gg.mineral.practice.commands.config;

import java.util.Locale;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

import lombok.val;

public class ArenaCommand extends PlayerCommand {

    public ArenaCommand() {
        super("arena", "practice.config");
    }

    private static final String CREATE = "create", SPAWN = "spawn", SETDISPLAY = "setdisplay", LIST = "list",
            TELEPORT = "teleport", TP = "tp", REMOVE = "remove", DELETE = "delete", PLACEHOLDER = "%arena%";

    private static final int CREATE_ARGS = 2, SPAWN_ARGS = 3, DISPLAY_ARGS = 2, TELEPORT_ARGS = 2, DELETE_ARGS = 2;

    @Override
    public void execute(final Player player, final String[] args) {
        switch (args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "") {
            default -> {
                ChatMessages.ARENA_COMMANDS.send(player);
                ChatMessages.ARENA_CREATE.send(player);
                ChatMessages.ARENA_SPAWN.send(player);
                ChatMessages.ARENA_DISPLAY.send(player);
                ChatMessages.ARENA_LIST.send(player);
                ChatMessages.ARENA_TP.send(player);
                ChatMessages.ARENA_DELETE.send(player);
            }
            case CREATE -> {
                if (args.length < CREATE_ARGS) {
                    UsageMessages.ARENA_CREATE.send(player);
                    return;
                }

                val arenaName = args[1];

                if (ArenaManager.getArenaByName(arenaName) != null) {
                    ErrorMessages.ARENA_ALREADY_EXISTS.send(player);
                    return;
                }

                val arena = new Arena(arenaName, ArenaManager.CURRENT_ID++);
                arena.setDefaults();
                ArenaManager.registerArena(
                        arena);
                ChatMessages.ARENA_CREATED.clone().replace(PLACEHOLDER, arenaName).send(player);
            }
            case SPAWN -> {
                if (args.length < SPAWN_ARGS) {
                    UsageMessages.ARENA_SPAWN.send(player);
                    return;
                }

                val arenaName = args[1];
                val arena = ArenaManager.getArenaByName(arenaName);

                if (arena == null) {
                    ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
                    return;
                }

                switch (args[2].toLowerCase(Locale.ROOT)) {
                    case "1" ->
                        arena.setLocation1(player.getLocation());
                    case "2" -> arena.setLocation2(player.getLocation());
                    case "waiting" -> arena.setWaitingLocation(player.getLocation());
                    default -> {
                        UsageMessages.ARENA_SPAWN.send(player);
                        return;
                    }
                }

                ChatMessages.ARENA_SPAWN_SET.clone().replace(PLACEHOLDER, arenaName).send(player);
            }
            case SETDISPLAY -> {
                if (args.length < DISPLAY_ARGS) {
                    UsageMessages.ARENA_DISPLAY.send(player);
                    return;
                }

                val arenaName = args[1];
                val arena = ArenaManager.getArenaByName(arenaName);

                if (arena == null) {
                    ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
                    return;
                }

                arena.setDisplayItem(player.getItemInHand());

                if (args.length > DISPLAY_ARGS)
                    arena.setDisplayName(args[2].replace("&", "§"));

                ChatMessages.ARENA_DISPLAY_SET.clone().replace(PLACEHOLDER, arenaName).send(player);
            }
            case LIST -> {
                val sb = new StringBuilder(CC.GRAY + "[");

                val iterator = ArenaManager.getArenas().values().iterator();

                while (iterator.hasNext()) {
                    val a = iterator.next();
                    sb.append(CC.GREEN + a.getName());

                    if (iterator.hasNext())
                        sb.append(CC.GRAY + ", ");
                }

                sb.append(CC.GRAY + "]");

                player.sendMessage(sb.toString());
            }
            case TELEPORT, TP -> {
                if (args.length < TELEPORT_ARGS) {
                    UsageMessages.ARENA_TP.send(player);
                    return;
                }

                val arena = ArenaManager.getArenaByName(args[1]);

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

            }
            case DELETE, REMOVE -> {
                if (args.length < DELETE_ARGS) {
                    UsageMessages.ARENA_DELETE.send(player);
                    return;
                }

                val arena = ArenaManager.getArenaByName(args[1]);

                if (arena == null) {
                    ErrorMessages.ARENA_DOES_NOT_EXIST.send(player);
                    return;
                }

                ArenaManager.remove(arena);
                val arenaName = args[1];
                ChatMessages.ARENA_DELETED.clone()
                        .replace(PLACEHOLDER, arenaName).send(player);
            }
        }
    }
}
