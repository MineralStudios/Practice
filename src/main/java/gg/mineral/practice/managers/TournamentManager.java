package gg.mineral.practice.managers;

import java.util.List;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.tournaments.Tournament;

public class TournamentManager {
	final static GlueList<Tournament> list = new GlueList<>();

	public static void register(Tournament tournament) {
		list.add(tournament);
	}

	public static void remove(Tournament tournament) {
		list.remove(tournament);
	}

	public static List<Tournament> list() {
		return list;
	}

	public static Tournament getByName(String s) {
		for (Tournament tournament : list()) {
			if (!tournament.getHost().equalsIgnoreCase(s)) {
				continue;
			}

			return tournament;
		}

		return null;
	}
}
