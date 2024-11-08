package gg.mineral.practice.managers;

import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

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
				count += match.getParticipants().size();

		return count;
	}

	@Nullable
	public static Match getMatchByParticipants(UUID... uuids) {
		for (Match match : matches)
			for (UUID uuid : uuids)
				if (match.getParticipants().get(uuid) != null)
					return match;

		return null;
	}

	@Nullable
	public static Match getMatchByParticipant(UUID uuid) {
		for (Match match : matches)
			if (match.getParticipants().get(uuid) != null)
				return match;

		return null;
	}
}
