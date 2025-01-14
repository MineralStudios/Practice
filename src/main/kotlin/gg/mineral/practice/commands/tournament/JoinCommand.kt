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
        if (profile.tournament != null) {
            profile.message(ErrorMessages.ALREADY_IN_TOURNAMENT)
            return
        }

        if (profile.event != null) {
            profile.message(ErrorMessages.ALREADY_IN_EVENT)
            return
        }

        if (profile.playerStatus !== PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)
            return
        }

        val event = getEventByName(name)

        event?.addPlayer(profile)

        val tournament = TournamentManager.getTournament(name)

        tournament?.addPlayer(profile)

        if (event == null && tournament == null) profile.message(ErrorMessages.EVENT_TOURNAMENT_NOT_EXIST)
    }
}
