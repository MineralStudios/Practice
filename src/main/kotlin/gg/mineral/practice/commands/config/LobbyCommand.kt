package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.appender.send
import gg.mineral.practice.managers.ProfileManager
import gg.mineral.practice.util.PlayerUtil
import gg.mineral.practice.util.messages.impl.ChatMessages
import gg.mineral.practice.util.world.SpawnLocation
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

@Command(name = "lobby")
@Permission("practice.config")
class LobbyCommand {
    @Execute
    fun execute(@Context sender: CommandSender, @Context location: SpawnLocation) {
        ProfileManager.spawnLocation = location
        sender.send(ChatMessages.SPAWN_SET)
    }

    @Execute(name = "teleport", aliases = ["tp"])
    fun executeTeleport(@Context player: Player) =
        PlayerUtil.teleport(player as CraftPlayer, ProfileManager.lobbyLocation)
}
