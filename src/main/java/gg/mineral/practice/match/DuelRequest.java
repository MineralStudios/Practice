package gg.mineral.practice.match;

import gg.mineral.practice.entity.Profile;

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
