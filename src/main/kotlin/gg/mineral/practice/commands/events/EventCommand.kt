package gg.mineral.practice.commands.events

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.menus.SelectEventMenu
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "event")
@Permission("practice.event")
class EventCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        if (profile.playerStatus !== PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)
            return
        }

        profile.openMenu(SelectEventMenu())
    }
}
