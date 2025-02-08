package gg.mineral.practice.commands.tournament

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.EventManager.getEventByName
import gg.mineral.practice.managers.TournamentManager
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "join")
class JoinCommand {
    @Execute
    fun execute(@Context profile: Profile, @Arg name: String) {
        profile.tournament?.let { return profile.message(ErrorMessages.ALREADY_IN_TOURNAMENT) }
        profile.event?.let { return profile.message(ErrorMessages.ALREADY_IN_EVENT) }
        if (profile.playerStatus !== PlayerStatus.IDLE) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

        val event = getEventByName(name)
        val tournament = TournamentManager.getTournament(name)

        if (event?.addPlayer(profile) == false && tournament?.addPlayer(profile) == false) profile.message(ErrorMessages.EVENT_TOURNAMENT_NOT_EXIST)
    }
}
