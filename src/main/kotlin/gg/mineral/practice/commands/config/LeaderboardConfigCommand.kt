package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.managers.LeaderboardManager
import gg.mineral.practice.managers.LeaderboardManager.displayItem
import gg.mineral.practice.managers.LeaderboardManager.enabled
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command(name = "leaderboardconfig", aliases = ["lbconfig"])
@Permission("practice.config")
class LeaderboardConfigCommand : CommandSenderAppender {

    @Execute(name = "enable")
    fun executeEnable(@Context sender: CommandSender, @Arg toggled: Boolean) {
        enabled = toggled
        sender.send(ChatMessages.LEADERBOARD_ENABLED.clone().replace("%toggled%", toggled.toString()))
    }

    @Execute(name = "setdisplay", aliases = ["display"])
    fun executeSetDisplay(@Context player: Player, @Arg displayName: Optional<String>) {
        displayName.ifPresent { LeaderboardManager.displayName = it.replace("&", "§") }
        displayItem = player.itemInHand
        player.send(ChatMessages.LEADERBOARD_DISPLAY_SET)
    }

    @Execute(name = "slot")
    fun executeSlot(@Context sender: CommandSender, @Arg slot: Int) {
        LeaderboardManager.slot = slot
        sender.send(ChatMessages.LEADERBOARD_SLOT_SET.clone().replace("%slot%", slot.toString()))
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.LEADERBOARD_COMMANDS,
            ChatMessages.LEADERBOARD_ENABLE,
            ChatMessages.LEADERBOARD_SLOT,
            ChatMessages.LEADERBOARD_DISPLAY
        )
    }
}
