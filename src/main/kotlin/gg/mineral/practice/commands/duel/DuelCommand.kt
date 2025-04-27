package gg.mineral.practice.commands.duel

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.SubmitAction
import gg.mineral.practice.inventory.menus.OtherPartiesMenu
import gg.mineral.practice.inventory.menus.SelectModeMenu
import gg.mineral.practice.util.messages.impl.ErrorMessages
import gg.mineral.practice.util.messages.impl.UsageMessages
import java.util.*

@Command(name = "duel", aliases = ["1v1", "d"])
class DuelCommand {

    @Execute
    fun execute(@Context profile: Profile, @Arg duelReceiver: Optional<Profile>) {
        if (profile.match?.ended == false) return profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)

        duelReceiver.ifPresentOrElse({
            if (profile == it) return@ifPresentOrElse profile.message(ErrorMessages.YOU_CAN_NOT_DUEL_YOURSELF)

            if (it.party?.isPartyLeader(it) != profile.party?.isPartyLeader(profile))
                return@ifPresentOrElse profile.message(if (it.party != null) ErrorMessages.PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER else ErrorMessages.NOT_IN_PARTY_OR_PARTY_LEADER)

            profile.duelRequestReciever = it
            profile.openMenu(SelectModeMenu(SubmitAction.DUEL))
        }, {
            if (profile.party == null) return@ifPresentOrElse profile.message(UsageMessages.DUEL)
            profile.openMenu(OtherPartiesMenu())
        })
    }
}
