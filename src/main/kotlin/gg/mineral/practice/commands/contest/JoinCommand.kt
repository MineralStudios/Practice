package gg.mineral.practice.commands.contest

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.events.Event
import gg.mineral.practice.managers.ContestManager.getByName
import gg.mineral.practice.tournaments.Tournament
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "join")
class JoinCommand {
    @Execute
    fun execute(@Context profile: Profile, @Arg name: String) {
        profile.contest?.let {
            return when (it) {
                is Tournament -> profile.message(ErrorMessages.ALREADY_IN_TOURNAMENT)
                is Event -> profile.message(ErrorMessages.ALREADY_IN_EVENT)
                else -> error("Unknown contest type")
            }
        }
        if (profile.playerStatus !== PlayerStatus.IDLE) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

        val contest = getByName(name)

        if (contest?.addPlayer(profile) == false) profile.message(ErrorMessages.EVENT_TOURNAMENT_NOT_EXIST)
    }
}
