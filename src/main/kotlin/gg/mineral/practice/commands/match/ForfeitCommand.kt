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
        if (profile.playerStatus !== PlayerStatus.FIGHTING) return profile.message(ErrorMessages.NOT_IN_MATCH)
        if (profile.match?.data?.ranked == true) return profile.message(ErrorMessages.CANNOT_FORFEIT_RANKED)

        profile.player.kill()
    }
}
