package gg.mineral.practice.commands.settings

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.menus.SettingsMenu

@Command(name = "settings")
class SettingsCommand {
    @Execute
    fun execute(@Context profile: Profile) = profile.openMenu(SettingsMenu())
}
