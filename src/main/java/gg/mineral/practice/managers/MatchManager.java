package gg.mineral.practice.managers;

import gg.mineral.practice.match.Match;
import gg.mineral.api.collection.GlueList;

public class MatchManager {
	GlueList<Match> list = new GlueList<>();

	public void registerMatch(Match match) {
		list.add(match);
	}

	public void remove(Match match) {
		list.remove(match);
	}

	public GlueList<Match> getMatchs() {
		return list;
	}
}
