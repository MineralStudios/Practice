package gg.mineral.practice.managers;

import gg.mineral.practice.match.Match;
import gg.mineral.api.collection.GlueList;

public class MatchManager {
	static GlueList<Match> list = new GlueList<>();

	public static void registerMatch(Match match) {
		list.add(match);
	}

	public static void remove(Match match) {
		list.remove(match);
	}

	public static GlueList<Match> getMatchs() {
		return list;
	}
}
