package ms.uk.eclipse.managers;

import land.strafe.api.collection.GlueList;
import ms.uk.eclipse.match.Match;

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
