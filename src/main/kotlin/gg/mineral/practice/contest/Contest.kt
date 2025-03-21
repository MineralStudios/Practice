package gg.mineral.practice.contest

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.battle.Battle
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.managers.ProfileManager.broadcast
import gg.mineral.practice.match.Match
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.util.collection.ProfileList
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.Bukkit

abstract class Contest(val id: String) : Battle {
    override val participants: ProfileList = ProfileList()
    abstract val maxPlayers: Int
    val matches: GlueList<Match> = GlueList()
    var forceStart: Boolean = false
    protected var started: Boolean = false
    override var ended: Boolean = false
        protected set
    private var round: Int = 1

    protected abstract val matchData: MatchData

    abstract fun addPlayer(p: Profile): Boolean
    abstract fun removePlayer(p: Profile)
    abstract fun startContest()

    protected abstract fun createMatch(p1: Profile, p2: Profile): Match

    protected open fun startRound() {
        if (participants.size == 1) {
            val winner = participants.first
            onContestWin(winner)
            return
        }
        if (participants.isEmpty()) {
            onContestEnd()
            return
        }
        val iterator = participants.iterator()
        while (iterator.hasNext()) {
            val p1 = iterator.next()
            if (!iterator.hasNext()) {
                p1.message(ChatMessages.NO_OPPONENT)
                break
            }
            val p2 = iterator.next()
            val match = createMatch(p1, p2)
            match.start()
            matches.add(match)
        }
    }

    fun removeMatch(m: Match) {
        matches.remove(m)
        if (ended) return
        if (matches.isEmpty()) {
            broadcastRoundOver(round)
            Bukkit.getScheduler().runTaskLater(PracticePlugin.INSTANCE, {
                startRound()
                round++
            }, 100)
        }
    }

    protected open fun broadcastRoundOver(round: Int) {
        broadcast(
            participants,
            ChatMessages.ROUND_OVER.clone().replace("%round%", round.toString())
        )
    }

    protected open fun onContestWin(winner: Profile?) {
        ended = true
    }

    protected open fun onContestEnd() {
        ended = true
    }
}
