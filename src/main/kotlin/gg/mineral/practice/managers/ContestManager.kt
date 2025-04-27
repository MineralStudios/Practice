package gg.mineral.practice.managers

import gg.mineral.practice.contest.Contest
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object ContestManager {
    private val contests: MutableMap<String, Contest> = Object2ObjectOpenHashMap()

    fun registerContest(contest: Contest) = contests.put(contest.id, contest)

    fun remove(contest: Contest) = contests.remove(contest.id)

    fun getByName(name: String) = contests[name]
}
