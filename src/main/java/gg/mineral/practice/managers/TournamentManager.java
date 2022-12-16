package gg.mineral.practice.managers;

import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.api.collection.GlueList;

public class TournamentManager {
	static GlueList<Tournament> tournaments = new GlueList<>();

	public static void registerTournament(Tournament tournament) {
		tournaments.add(tournament);
	}

	public static void remove(Tournament tournament) {
		tournaments.remove(tournament);
	}

	public static Tournament getTournamentByName(String s) {
		for (Tournament tournament : tournaments) {
			if (tournament.getHost().equalsIgnoreCase(s)) {
				return tournament;
			}
		}
		return null;
	}
}
