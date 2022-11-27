package gg.mineral.practice.queue;

import java.util.Map.Entry;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.MatchData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class QueueSearchTask {
	static Object2ObjectOpenHashMap<Profile, QueueEntry> map = new Object2ObjectOpenHashMap<>();

	public static void addPlayer(Profile player, QueueEntry qe) {
		Profile found = searchForMatch(qe);

		if (found == null) {
			map.put(player, qe);
			return;
		}

		removePlayer(found);
		Match m = new Match(player, found, new MatchData(qe));
		m.start();
	}

	public static void removePlayer(Profile player) {
		map.remove(player);
	}

	private static Profile searchForMatch(QueueEntry qd) {

		for (Entry<Profile, QueueEntry> e : map.entrySet()) {
			if (e.getValue().equals(qd)) {
				return e.getKey();
			}
		}

		return null;
	}

	public static int getNumberInQueue(Queuetype q, Gametype g) {
		int i = 0;

		for (QueueEntry qe : map.values()) {
			if (qe.getGametype().equals(g) && qe.getQueuetype().equals(q)) {
				i++;
			}
		}

		return i;
	}
}
