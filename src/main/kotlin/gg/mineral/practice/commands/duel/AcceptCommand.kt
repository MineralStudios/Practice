package gg.mineral.practice.commands.duel

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.match.Match
import gg.mineral.practice.match.TeamMatch
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "accept")
class AcceptCommand {
    @Execute
    fun execute(@Context profile: Profile, @Arg duelSender: Profile) {
        if (profile.playerStatus === PlayerStatus.QUEUEING) profile.removeFromQueue()
        if (duelSender.playerStatus === PlayerStatus.QUEUEING) duelSender.removeFromQueue()

        if (profile.playerStatus !== PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY)
            return
        }

        if (duelSender.playerStatus !== PlayerStatus.IDLE) {
            profile.message(ErrorMessages.DUEL_SENDER_NOT_IN_LOBBY)
            return
        }

        if ((duelSender.party != null) != (profile.party != null)) {
            if (profile.party != null) profile.message(ErrorMessages.PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER)
            else profile.message(ErrorMessages.PLAYER_IN_PARTY)
            return
        }

        val it = profile.recievedDuelRequests.entryIterator()

        while (it.hasNext()) {
            val duelRequest = it.next().key

            if (duelRequest.sender != duelSender) continue

            it.remove()
            val matchData = MatchData(duelRequest.duelSettings)
            val match =
                duelSender.party?.let { it1 -> profile.party?.let { it2 -> TeamMatch(it1, it2, matchData) } } ?: Match(
                    matchData,
                    duelSender,
                    profile
                )
            match.start()
            return
        }
    }
}
