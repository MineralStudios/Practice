package gg.mineral.practice.commands.contest

import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.flag.Flag
import dev.rollczi.litecommands.annotations.permission.Permission
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.SubmitAction
import gg.mineral.practice.inventory.menus.SelectModeMenu
import gg.mineral.practice.tournaments.AutomatedTournament
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.ListElementMessage
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "tournament")
@Permission("practice.event")
class TournamentCommand {
    @Execute
    fun execute(@Context profile: Profile, @Flag("-d") discord: Boolean) {
        if (profile.contest != null) return profile.message(ErrorMessages.ALREADY_IN_TOURNAMENT)
        if (discord && profile.player?.isOp == true) {
            AutomatedTournament(profile.name).startContest()
            return
        }
        if (profile.playerStatus !== PlayerStatus.IDLE) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

        profile.openMenu(SelectModeMenu(SubmitAction.TOURNAMENT))
    }

    @Execute(name = "forcestart")
    fun forceStart(@Context profile: Profile) {
        profile.contest?.let {
            if (it.id == profile.name)
                it.forceStart = true
        } ?: profile.message(ErrorMessages.NOT_IN_TOURNAMENT)
    }

    @Execute(name = "status")
    fun status(@Context profile: Profile) {
        profile.contest?.let {
            for (match in it.matches) {
                val profile1 = match.profile1
                val profile2 = match.profile2
                if (profile1 == null || profile2 == null)
                    continue

                profile.message(ListElementMessage(profile1.name + " vs " + profile2.name, CC.GOLD))
            }
        } ?: profile.message(ErrorMessages.NOT_IN_TOURNAMENT)
    }
}
