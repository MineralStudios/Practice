package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.managers.KitEditorManager
import gg.mineral.practice.managers.KitEditorManager.displayItem
import gg.mineral.practice.managers.KitEditorManager.enabled
import gg.mineral.practice.managers.KitEditorManager.location
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command(name = "kiteditor")
@Permission("practice.config")
class KitEditorCommand : CommandSenderAppender {

    @Execute(name = "enable")
    fun executeEnable(@Context sender: CommandSender, @Arg toggled: Boolean) {
        enabled = toggled
        sender.send(
            ChatMessages.KIT_EDITOR_ENABLED.clone()
                .replace("%toggled%", toggled.toString())
        )
    }

    @Execute(name = "setdisplay")
    fun executeSetDisplay(@Context player: Player, @Arg displayName: Optional<String>) {
        displayName.ifPresent { KitEditorManager.displayName = it.replace("&", "§") }

        displayItem = player.itemInHand

        player.send(ChatMessages.KIT_EDITOR_DISPLAY_SET)
    }

    @Execute(name = "slot")
    fun executeSlot(@Context sender: CommandSender, @Arg slot: Int) {
        KitEditorManager.slot = slot
        sender.send(ChatMessages.KIT_EDITOR_SLOT_SET.clone().replace("%slot%", slot.toString()))
    }

    @Execute(name = "setlocation", aliases = ["location"])
    fun executeLocation(@Context player: Player) {
        location = player.location
        player.send(ChatMessages.KIT_EDITOR_LOCATION_SET)
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.KIT_EDITOR_COMMANDS,
            ChatMessages.KIT_EDITOR_ENABLE,
            ChatMessages.KIT_EDITOR_DISPLAY,
            ChatMessages.KIT_EDITOR_SLOT,
            ChatMessages.KIT_EDITOR_LOCATION
        )
    }
}
