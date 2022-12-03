package gg.mineral.practice.request;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.data.MatchData;

public class DuelRequest {
	final MatchData matchData;
	final Profile sender;

	public DuelRequest(Profile p, MatchData matchData) {
		this.matchData = matchData;
		this.sender = p;
	}

	public MatchData getMatchData() {
		return matchData;
	}

	public Profile getSender() {
		return sender;
	}
}
