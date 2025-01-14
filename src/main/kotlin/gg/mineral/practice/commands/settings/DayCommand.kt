package gg.mineral.practice.commands.settings

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.entity.Player

@Command(name = "day")
class DayCommand : CommandSenderAppender {
    @Execute
    fun execute(@Context player: Player) {
        player.send(ChatMessages.TIME_SET_DAY)
        player.setPlayerTime(0L, false)
    }
}