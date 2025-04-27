package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.managers.SpectateManager
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command(name = "spectateconfig", aliases = ["specconfig"])
@Permission("practice.config")
class SpectateConfigCommand : CommandSenderAppender {

    @Execute(name = "enable")
    fun executeEnable(@Context sender: CommandSender, @Arg toggled: Boolean) {
        SpectateManager.enabled = toggled
        sender.send(ChatMessages.SPECTATE_ENABLED.clone().replace("%toggled%", toggled.toString()))
    }

    @Execute(name = "setdisplay", aliases = ["display"])
    fun executeSetDisplay(@Context player: Player, @Arg displayName: Optional<String>) {
        displayName.ifPresent { SpectateManager.displayName = it.replace("&", "§") }
        SpectateManager.displayItem = player.itemInHand
        player.send(ChatMessages.SPECTATE_DISPLAY_SET)
    }

    @Execute(name = "slot")
    fun executeSlot(@Context sender: CommandSender, @Arg slot: Int) {
        SpectateManager.slot = slot
        sender.send(ChatMessages.SPECTATE_SLOT_SET.clone().replace("%slot%", slot.toString()))
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.SPECTATE_COMMANDS,
            ChatMessages.SPECTATE_ENABLE,
            ChatMessages.SPECTATE_SLOT,
            ChatMessages.SPECTATE_DISPLAY
        )
    }
}
