package gg.mineral.practice.managers;

import java.util.List;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.match.Match;

public class MatchManager {
	final static GlueList<Match> list = new GlueList<>();

	public static void register(Match match) {
		list.add(match);
	}

	public static void remove(Match match) {
		list.remove(match);
	}

	public static List<Match> list() {
		return list;
	}
}
