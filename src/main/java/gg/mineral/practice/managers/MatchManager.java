package gg.mineral.practice.managers;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.QueueMatchData;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.Queuetype;
import lombok.Getter;

public class MatchManager {
	@Getter
	static GlueList<Match<?>> matches = new GlueList<>();

	public static void registerMatch(Match<?> match) {
		matches.add(match);
	}

	public static void remove(Match<?> match) {
		matches.remove(match);
	}

	public static int getInGameCount(Queuetype queuetype, Gametype gametype) {
		int inGame = 0;
		for (Match<?> match : getMatches()) {
			if (match.getData() instanceof QueueMatchData qData) {
				QueueEntry queueEntry = qData.getQueueEntry();

				if (queueEntry == null)
					continue;

				if (queueEntry.getGametype().equals(gametype) && queueEntry.getQueuetype().equals(queuetype))
					inGame++;
			}
		}
		return inGame;
	}
}
