package gg.mineral.practice.managers

import gg.mineral.practice.tournaments.Tournament
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

object TournamentManager {
    private val tournaments: MutableMap<String, Tournament> = Object2ObjectOpenHashMap()

    fun registerTournament(tournament: Tournament) {
        tournaments[tournament.host] = tournament
    }

    fun remove(tournament: Tournament) = tournaments.remove(tournament.host)

    fun getTournament(name: String) = tournaments[name]
}
