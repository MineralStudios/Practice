package gg.mineral.practice.commands.config

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.appender.CommandSenderAppender
import gg.mineral.practice.managers.PlayerSettingsManager
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

@Command(name = "settingsconfig")
@Permission("practice.config")
class SettingsConfigCommand : CommandSenderAppender {
    @Execute(name = "enable")
    fun executeEnable(@Context sender: CommandSender, @Arg toggled: Boolean) {
        PlayerSettingsManager.enabled = toggled
        sender.send(ChatMessages.SETTINGS_ENABLED.clone().replace("%toggled%", toggled.toString()))
    }

    @Execute(name = "setdisplay")
    fun executeSetDisplay(@Context player: Player, @Arg displayName: Optional<String>) {
        displayName.ifPresent { PlayerSettingsManager.displayName = it.replace("&", "§") }
        PlayerSettingsManager.displayItem = player.itemInHand
        player.send(ChatMessages.SETTINGS_DISPLAY_SET)
    }

    @Execute(name = "slot")
    fun executeSlot(@Context sender: CommandSender, @Arg slot: Int) {
        PlayerSettingsManager.slot = slot
        sender.send(ChatMessages.SETTINGS_SLOT_SET.clone().replace("%slot%", slot.toString()))
    }

    @Execute(aliases = ["help"])
    fun executeHelp(@Context sender: CommandSender) {
        sender.send(
            ChatMessages.SETTINGS_COMMANDS,
            ChatMessages.SETTINGS_ENABLE,
            ChatMessages.SETTINGS_SLOT,
            ChatMessages.SETTINGS_DISPLAY
        )
    }
}
