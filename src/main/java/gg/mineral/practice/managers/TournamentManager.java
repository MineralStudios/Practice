package gg.mineral.practice.managers;

import gg.mineral.practice.tournaments.Tournament;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;

public class TournamentManager {
    private final static Map<String, Tournament> tournaments = new Object2ObjectOpenHashMap<>();

    public static void registerTournament(Tournament tournament) {
        tournaments.put(tournament.getHost(), tournament);
    }

    public static void remove(Tournament tournament) {
        tournaments.remove(tournament.getHost());
    }

    public static Tournament getTournament(String name) {
        return tournaments.get(name);
    }
}
