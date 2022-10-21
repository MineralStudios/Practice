package gg.mineral.practice.queue;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map.Entry;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.MatchData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class QueueSearchTask {
	static Object2ObjectOpenHashMap<Profile, QueueEntry> map = new Object2ObjectOpenHashMap<>();

	public static void addPlayer(Profile player, QueueEntry qe) throws SQLException {
		Profile found = searchForMatch(qe);

		if (found == null) {
			map.put(player, qe);
			return;
		}

		Match m = new Match(player, found, new MatchData(qe));
		m.start();
	}

	public static void removePlayer(Profile player) {
		map.remove(player);
	}

	private static Profile searchForMatch(QueueEntry queueEntry) {

		Iterator<Entry<Profile, QueueEntry>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<Profile, QueueEntry> entry = iterator.next();

			if (!entry.getValue().equals(queueEntry)) {
				continue;
			}

			iterator.remove();
			return entry.getKey();
		}

		return null;
	}

	public static long getNumberInQueue(Queuetype queuetype, Gametype gametype) {
		return map.values().stream().filter(
				queueEntry -> queueEntry.getGametype().equals(gametype) && queueEntry.getQueuetype().equals(queuetype))
				.count();
	}
}
