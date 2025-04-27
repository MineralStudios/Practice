package gg.mineral.practice.managers

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.match.Match
import gg.mineral.practice.queue.Queuetype

object MatchManager {
    var matches: GlueList<Match> = GlueList()

    fun registerMatch(match: Match) = matches.add(match)

    fun remove(match: Match) = matches.remove(match)

    fun getInGameCount(queuetype: Queuetype, gametype: Gametype): Int {
        val queueAndGameTypeHash = (queuetype.id.toInt() shl 8 or gametype.id.toInt()).toShort()
        var count = 0
        for (match in matches) if (match.data.queueAndGameTypeHash == queueAndGameTypeHash) count += match.participants.size

        return count
    }
}
