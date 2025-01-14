package gg.mineral.practice.commands.duel

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.SubmitAction
import gg.mineral.practice.inventory.menus.OtherPartiesMenu
import gg.mineral.practice.inventory.menus.SelectModeMenu
import gg.mineral.practice.util.messages.impl.ErrorMessages
import gg.mineral.practice.util.messages.impl.UsageMessages
import java.util.*

@Command(name = "duel")
class DuelCommand {

    @Execute
    fun execute(@Context profile: Profile, @Arg duelReceiver: Optional<Profile>) {
        if (profile.playerStatus !== PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)
            return
        }

        duelReceiver.ifPresentOrElse({ receiver ->
            if (profile == receiver) {
                profile.message(ErrorMessages.YOU_CAN_NOT_DUEL_YOURSELF)
                return@ifPresentOrElse
            }
            profile.party?.let {
                if (!(receiver.party?.partyLeader == receiver
                            && it.partyLeader == profile)
                ) {
                    profile.message(ErrorMessages.PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER)
                    return@ifPresentOrElse
                }
            } ?: run {
                if (receiver.party != null) {
                    profile.message(ErrorMessages.PLAYER_IN_PARTY)
                    return@ifPresentOrElse
                }
            }

            profile.duelRequestReciever = receiver
            profile.openMenu(SelectModeMenu(SubmitAction.DUEL))
        }, {
            if (profile.party == null) {
                profile.message(UsageMessages.DUEL)
                return@ifPresentOrElse
            }

            profile.openMenu(OtherPartiesMenu())
            return@ifPresentOrElse
        })
    }
}
