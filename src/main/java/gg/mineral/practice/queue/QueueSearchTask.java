package gg.mineral.practice.queue;

import java.util.Map.Entry;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.MatchData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class QueueSearchTask {
	static Object2ObjectOpenHashMap<Profile, QueueEntry> map = new Object2ObjectOpenHashMap<>();

	public static void addPlayer(Profile profile, QueueEntry queueEntry) {
		Profile found = searchForMatch(queueEntry);

		if (found == null) {
			map.put(profile, queueEntry);
			return;
		}

		removePlayer(found);
		Match m = new Match(profile, found, new MatchData(queueEntry));
		m.start();
	}

	public static void removePlayer(Profile profile) {
		map.remove(profile);
	}

	private static Profile searchForMatch(QueueEntry queueEntry) {

		for (Entry<Profile, QueueEntry> e : map.entrySet()) {
			if (e.getValue().equals(queueEntry)) {
				return e.getKey();
			}
		}

		return null;
	}

	public static int getNumberInQueue(Queuetype queuetype, Gametype gametype) {
		int i = 0;

		for (QueueEntry queueEntry : map.values()) {
			if (queueEntry.getGametype().equals(gametype) && queueEntry.getQueuetype().equals(queuetype)) {
				i++;
			}
		}

		return i;
	}
}
