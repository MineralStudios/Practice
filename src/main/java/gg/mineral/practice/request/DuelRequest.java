package gg.mineral.practice.request;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.data.MatchData;

public class DuelRequest {
	final MatchData m;
	final Profile sender;

	public DuelRequest(Profile p, MatchData m) {
		this.m = m;
		this.sender = p;
	}

	public MatchData getMatchData() {
		return m;
	}

	public Profile getSender() {
		return sender;
	}
}
