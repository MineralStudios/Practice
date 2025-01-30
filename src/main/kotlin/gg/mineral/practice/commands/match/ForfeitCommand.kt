package gg.mineral.practice.commands.match

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.kill
import gg.mineral.practice.util.messages.impl.ErrorMessages


@Command(name = "forfeit", aliases = ["l", "giveup"])
class ForfeitCommand {
    @Execute
    fun execute(@Context profile: Profile) {
        if (profile.playerStatus !== PlayerStatus.FIGHTING) {
            profile.message(ErrorMessages.NOT_IN_MATCH)
            return
        }

        if (profile.match?.data?.ranked == true) {
            profile.message(ErrorMessages.CANNOT_FORFEIT_RANKED)
            return
        }

        profile.player.kill()
    }
}
