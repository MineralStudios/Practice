package ms.uk.eclipse.managers;

import land.strafe.api.collection.GlueList;
import ms.uk.eclipse.tournaments.Tournament;

public class TournamentManager {
	GlueList<Tournament> list = new GlueList<>();

	public void registerTournament(Tournament tournament) {
		list.add(tournament);
	}

	public void remove(Tournament tournament) {
		list.remove(tournament);
	}

	public GlueList<Tournament> getTournaments() {
		return list;
	}

	public Tournament getTournamentByName(String s) {
		for (Tournament t : list) {
			if (t.getHost().equalsIgnoreCase(s)) {
				return t;
			}
		}
		return null;
	}
}
