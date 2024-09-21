package gg.mineral.practice.queue;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.QueueMatchData;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class QueueSearchTask {
	static Object2ObjectOpenHashMap<Profile, List<QueueEntry>> map = new Object2ObjectOpenHashMap<>();

	public static void addPlayer(Profile profile, QueueEntry queueEntry) {
		Profile found = searchForMatch(queueEntry);

		QueueMatchData matchData = null;

		if (found != null) {
			Object2BooleanOpenHashMap<Arena> profileEnabledArenas = profile.getMatchData().getEnabledArenas();
			profileEnabledArenas.object2BooleanEntrySet().removeIf(e -> !e.getBooleanValue());
			Object2BooleanOpenHashMap<Arena> foundEnabledArenas = found.getMatchData().getEnabledArenas();
			foundEnabledArenas.object2BooleanEntrySet().removeIf(e -> !e.getBooleanValue());

			Object2BooleanOpenHashMap<Arena> commonEnabledArenas = new Object2BooleanOpenHashMap<>(
					profileEnabledArenas);
			commonEnabledArenas.object2BooleanEntrySet().removeIf(e -> !foundEnabledArenas.getBoolean(e.getKey()));

			matchData = new QueueMatchData(queueEntry, commonEnabledArenas);
		}

		if (found == null) {
			List<QueueEntry> queueEntries = map.getOrDefault(profile, new GlueList<>());
			queueEntries.add(queueEntry);
			map.put(profile, queueEntries);
			return;
		}

		removePlayer(found);
		Match<QueueMatchData> m = new Match<>(profile, found,
				matchData);
		m.start();
	}

	public static void removePlayer(Profile profile) {
		map.remove(profile);
	}

	public static boolean removePlayer(Profile profile, QueueEntry queueEntry) {
		List<QueueEntry> queueEntries = map.get(profile);

		if (queueEntries == null)
			return true;

		queueEntries.remove(queueEntry);

		map.put(profile, queueEntries.isEmpty() ? null : queueEntries);

		return queueEntries.isEmpty();
	}

	public static List<QueueEntry> getQueueEntries(Profile profile) {
		return map.get(profile);
	}

	@Nullable
	private static Profile searchForMatch(QueueEntry queueEntry) {
		for (Entry<Profile, List<QueueEntry>> e : map.entrySet())
			if (e.getValue().contains(queueEntry))
				if (e.getKey().getPlayerStatus() == PlayerStatus.QUEUEING)
					return e.getKey();

		return null;
	}

	public static int getNumberInQueue(Queuetype queuetype, Gametype gametype) {
		int i = 0;

		for (List<QueueEntry> queueEntries : map.values())
			for (QueueEntry queueEntry : queueEntries)
				if (queueEntry.getGametype().equals(gametype) && queueEntry.getQueuetype().equals(queuetype))
					i++;

		return i;
	}
}
