package gg.mineral.practice.request;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.data.MatchData;
import lombok.Getter;

public class DuelRequest {
	@Getter
	final MatchData matchData;
	@Getter
	final Profile sender;

	public DuelRequest(Profile sender, MatchData matchData) {
		this.matchData = matchData;
		this.sender = sender;
	}
}
