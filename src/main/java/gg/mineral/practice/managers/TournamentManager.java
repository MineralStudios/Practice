package gg.mineral.practice.managers;

import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.api.collection.GlueList;

public class TournamentManager {
	static GlueList<Tournament> list = new GlueList<>();

	public static void registerTournament(Tournament tournament) {
		list.add(tournament);
	}

	public static void remove(Tournament tournament) {
		list.remove(tournament);
	}

	public GlueList<Tournament> getTournaments() {
		return list;
	}

	public static Tournament getTournamentByName(String s) {
		for (Tournament tournament : list) {
			if (tournament.getHost().equalsIgnoreCase(s)) {
				return tournament;
			}
		}
		return null;
	}
}
