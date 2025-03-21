package gg.mineral.practice.events

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.match.data.MatchData
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap

class HostedEvent(hostProfile: Profile, eventArenaId: Byte) : Event(
    hostProfile.name,
    matchData = MatchData(hostProfile.duelSettings, Byte2BooleanOpenHashMap().apply { put(eventArenaId, true) })
) {
    init {
        addPlayer(hostProfile)
    }

    override fun onCountdownStart(profile: Profile) {
    }
}