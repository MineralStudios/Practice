package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.managers.PartyManager
import gg.mineral.practice.managers.PartyManager.displayItem
import gg.mineral.practice.managers.PartyManager.enabled
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command(name = "parties")
@Permission("practice.config")
class PartiesCommand : CommandSenderAppender {

    @Execute(name = "enable")
    fun executeEnable(@Context sender: CommandSender, @Arg toggled: Boolean) {
        enabled = toggled
        sender.send(ChatMessages.PARTIES_ENABLED.clone().replace("%toggled%", toggled.toString()))
    }

    @Execute(name = "setdisplay", aliases = ["display"])
    fun executeSetDisplay(@Context player: Player, @Arg displayName: Optional<String>) {
        displayName.ifPresent { PartyManager.displayName = it.replace("&", "§") }
        displayItem = player.itemInHand
        player.send(ChatMessages.PARTIES_DISPLAY_SET)
    }

    @Execute(name = "slot")
    fun executeSlot(@Context sender: CommandSender, @Arg slot: Int) {
        PartyManager.slot = slot
        sender.send(ChatMessages.PARTIES_SLOT_SET.clone().replace("%slot%", slot.toString()))
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.PARTIES_COMMANDS,
            ChatMessages.PARTIES_ENABLE,
            ChatMessages.PARTIES_DISPLAY,
            ChatMessages.PARTIES_SLOT
        )
    }
}
