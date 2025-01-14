package gg.mineral.practice.commands.tournament

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.SubmitAction
import gg.mineral.practice.inventory.menus.SelectModeMenu
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "tournament")
@Permission("practice.event")
class TournamentCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        if (profile.playerStatus !== PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)
            return
        }

        profile.openMenu(SelectModeMenu(SubmitAction.TOURNAMENT))
    }
}
