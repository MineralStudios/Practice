package gg.mineral.practice.managers;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.match.Match;
import lombok.Getter;

public class MatchManager {
	@Getter
	static GlueList<Match> matches = new GlueList<>();

	public static void registerMatch(Match match) {
		matches.add(match);
	}

	public static void remove(Match match) {
		matches.remove(match);
	}
}
