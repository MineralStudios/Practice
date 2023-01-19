package gg.mineral.practice.queue;

import java.util.List;
import java.util.Map.Entry;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.MatchData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class QueueSearchTask {
	static Object2ObjectOpenHashMap<Profile, List<QueueEntry>> map = new Object2ObjectOpenHashMap<>();

	public static void addPlayer(Profile profile, QueueEntry queueEntry) {
		Profile found = searchForMatch(queueEntry);

		if (found == null) {
			List<QueueEntry> queueEntries = map.getOrDefault(profile, new GlueList<>());
			queueEntries.add(queueEntry);
			map.put(profile, queueEntries);
			return;
		}

		removePlayer(found);
		Match m = new Match(profile, found, new MatchData(queueEntry));
		m.start();
	}

	public static void removePlayer(Profile profile) {
		map.remove(profile);
	}

	public static boolean removePlayer(Profile profile, QueueEntry queueEntry) {
		List<QueueEntry> queueEntries = map.get(profile);

		if (queueEntries == null) {
			return true;
		}

		queueEntries.remove(queueEntry);
		map.put(profile, queueEntries);
		return queueEntries.isEmpty();
	}

	public static List<QueueEntry> getQueueEntries(Profile profile) {
		return map.get(profile);
	}

	private static Profile searchForMatch(QueueEntry queueEntry) {

		for (Entry<Profile, List<QueueEntry>> e : map.entrySet()) {
			if (e.getValue().contains(queueEntry)) {
				return e.getKey();
			}
		}

		return null;
	}

	public static int getNumberInQueue(Queuetype queuetype, Gametype gametype) {
		int i = 0;

		for (List<QueueEntry> queueEntries : map.values()) {
			for (QueueEntry queueEntry : queueEntries) {
				if (queueEntry.getGametype().equals(gametype) && queueEntry.getQueuetype().equals(queuetype)) {
					i++;
				}
			}
		}

		return i;
	}
}
