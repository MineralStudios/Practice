package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.arena.Arena
import gg.mineral.practice.arena.ArenaImpl
import gg.mineral.practice.arena.EventArena
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.ArenaManager.getArenaByName
import gg.mineral.practice.managers.ArenaManager.registerArena
import gg.mineral.practice.managers.ArenaManager.remove
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.messages.impl.ErrorMessages
import gg.mineral.practice.util.messages.impl.UsageMessages
import gg.mineral.practice.util.world.SpawnLocation
import gg.mineral.practice.util.world.appender.LocationAppender
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

@Command(name = "arena")
@Permission("practice.config")
class ArenaCommand : CommandSenderAppender, LocationAppender {
    @Execute(name = "create")
    fun executeCreate(@Context sender: CommandSender, @Arg name: String) {
        if (getArenaByName(name) != null) {
            sender.send(ErrorMessages.ARENA_ALREADY_EXISTS)
            return
        }

        registerArena(ArenaImpl(name, ArenaManager.CURRENT_ID++))
        sender.send(ChatMessages.ARENA_CREATED.clone().replace(PLACEHOLDER, name))
    }

    @Execute(name = "spawn")
    fun executeSpawn(
        @Context sender: CommandSender,
        @Context location: SpawnLocation,
        @Arg arena: Arena,
        @Arg spawn: String
    ) {
        when (spawn.lowercase()) {
            "1" -> arena.location1 = location
            "2" -> arena.location2 = location
            "waiting" -> (arena as? EventArena)?.waitingLocation = location
            else -> {
                sender.send(UsageMessages.ARENA_SPAWN)
                return
            }
        }

        sender.send(ChatMessages.ARENA_SPAWN_SET.clone().replace(PLACEHOLDER, arena.name))
    }

    @Execute(name = "edit")
    fun executeEdit() {

    }

    @Execute(name = "setdisplay", aliases = ["display"])
    fun executeSetDisplay(@Context player: Player, @Arg arena: Arena, @Arg displayName: Optional<String>) {
        arena.displayItem = player.itemInHand
        displayName.ifPresent { arena.displayName = it.replace("&", "§") }
        player.send(ChatMessages.ARENA_DISPLAY_SET.clone().replace(PLACEHOLDER, arena.name))
    }

    @Execute(name = "list")
    fun executeList(@Context sender: CommandSender) {
        val sb = StringBuilder(CC.GRAY + "[")

        val iterator = arenas.values.iterator()

        while (iterator.hasNext()) {
            iterator.next()?.let {
                sb.append(CC.GREEN).append(it.name)
                if (iterator.hasNext()) sb.append(CC.GRAY).append(", ")
            }
        }

        sb.append(CC.GRAY).append("]")

        sender.sendMessage(sb.toString())
    }

    @Execute(name = "teleport", aliases = ["tp"])
    fun executeTeleport(@Context player: Player, @Arg arena: Arena) {
        try {
            val world = arena.generateBaseWorld()
            PlayerUtil.teleport(player as CraftPlayer, arena.location1.bukkit(world))
        } catch (e: Exception) {
            ErrorMessages.CANNOT_TELEPORT_TO_ARENA.send(player)
            e.printStackTrace()
        }
    }

    @Execute(name = "remove", aliases = ["delete"])
    fun executeRemove(@Context sender: CommandSender, @Arg arena: Arena) {
        remove(arena)
        sender.send(ChatMessages.ARENA_DELETED.clone().replace(PLACEHOLDER, arena.name))
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.ARENA_COMMANDS,
            ChatMessages.ARENA_CREATE,
            ChatMessages.ARENA_SPAWN,
            ChatMessages.ARENA_DISPLAY,
            ChatMessages.ARENA_LIST,
            ChatMessages.ARENA_TP,
            ChatMessages.ARENA_DELETE
        )
    }

    companion object {
        private const val PLACEHOLDER = "%arena%"
    }
}
