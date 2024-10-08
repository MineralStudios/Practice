package gg.mineral.practice.managers;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.queue.Queuetype;
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

	public static int getInGameCount(Queuetype queuetype, Gametype gametype) {
		short queueAndGameTypeHash = (short) (queuetype.getId() << 8 | gametype.getId());
		int count = 0;
		for (Match match : matches)
			if (match.getData().getQueueAndGameTypeHash() == queueAndGameTypeHash)
				count++;

		return count;
	}
}
