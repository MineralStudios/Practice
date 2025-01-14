package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.managers.ProfileManager
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.Location
import org.bukkit.command.CommandSender

@Command(name = "lobby")
@Permission("practice.config")
class LobbyCommand : CommandSenderAppender {
    @Execute
    fun execute(@Context sender: CommandSender, @Context location: Location) {
        location.world.setSpawnLocation(location.blockX, location.blockY, location.blockZ)
        ProfileManager.spawnLocation = location
        sender.send(ChatMessages.SPAWN_SET)
    }
}
