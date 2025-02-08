package gg.mineral.practice.commands.spectator

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.menus.SpectateMenu
import gg.mineral.practice.util.messages.impl.ErrorMessages
import java.util.*

@Command(name = "spectate", aliases = ["spec"])
class SpectateCommand {
    @Execute
    fun execute(@Context profile: Profile, @Arg profileToSpectate: Optional<Profile>) {
        if (profile.playerStatus !== PlayerStatus.IDLE) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

        profileToSpectate.ifPresentOrElse({
            if (profile == it) profile.message(ErrorMessages.NOT_SPEC_SELF)
            else profile.spectate(it)
        }, { profile.openMenu(SpectateMenu()) })
    }
}
