package gg.mineral.practice.inventory

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.match.TeamMatch
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.tournaments.HostedTournament
import gg.mineral.practice.util.messages.impl.ErrorMessages

enum class SubmitAction {
    DUEL {
        override fun execute(profile: Profile) {
            profile.player?.closeInventory()
            profile.duelRequestReciever?.let { profile.sendDuelRequest(it) }
        }
    },
    P_SPLIT {
        override fun execute(profile: Profile) {
            profile.player?.closeInventory()

            profile.party?.let {
                if (it.partyLeader != profile) {
                    profile.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER)
                    return
                }

                if (it.partyMembers.size < 2) {
                    profile.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH)
                    return
                }

                val partyMatch = TeamMatch(it, MatchData(profile.duelSettings))
                partyMatch.start()
            }
        }
    },
    TOURNAMENT {
        override fun execute(profile: Profile) {
            profile.player?.closeInventory()
            HostedTournament(profile).startContest()
        }
    };

    abstract fun execute(profile: Profile)
}
