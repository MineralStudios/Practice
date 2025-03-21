package gg.mineral.practice.tournaments

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.match.data.MatchData

class HostedTournament(hostProfile: Profile) :
    Tournament(hostProfile.name, matchData = MatchData(hostProfile.duelSettings)) {
    override fun onCountdownStart(profile: Profile) {
    }

    init {
        addPlayer(hostProfile)
    }
}