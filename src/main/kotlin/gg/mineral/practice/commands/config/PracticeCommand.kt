package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.appender.send
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.command.CommandSender

@Command(name = "practice", aliases = ["practiceconfig", "practicecommandslist"])
@Permission("practice.config")
class PracticeCommand {
    @Execute
    fun execute(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.CONFIG_COMMANDS,
            ChatMessages.QUEUETYPE,
            ChatMessages.GAMETYPE,
            ChatMessages.CATEGORY,
            ChatMessages.EVENTS,
            ChatMessages.ARENA,
            ChatMessages.KIT_EDITOR,
            ChatMessages.PARTIES,
            ChatMessages.LOBBY,
            ChatMessages.SETTINGS_CONFIG
        )
    }
}
