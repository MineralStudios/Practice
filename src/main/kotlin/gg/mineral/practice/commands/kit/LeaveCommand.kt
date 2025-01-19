package gg.mineral.practice.commands.kit

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "leave")
class LeaveCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        if (profile.playerStatus !== PlayerStatus.KIT_CREATOR
            && profile.playerStatus !== PlayerStatus.KIT_EDITOR
        ) {
            profile.message(ErrorMessages.NOT_IN_KIT_EDITOR_OR_CREATOR)
            return
        }

        profile.kitCreator = null
        profile.kitEditor = null
    }
}
