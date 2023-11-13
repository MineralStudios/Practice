package gg.mineral.practice.request;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.data.MatchData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DuelRequest {
	@Getter
	final Profile sender;
	@Getter
	final MatchData matchData;
}
