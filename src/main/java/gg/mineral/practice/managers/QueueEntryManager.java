package gg.mineral.practice.managers;

import java.util.List;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.Queuetype;

public class QueueEntryManager {
    static final GlueList<QueueEntry> list = new GlueList<>();

    public static void register(QueueEntry queueEntry) {
        list.add(queueEntry);
    }

    public static QueueEntry getOrCreate(Queuetype queuetype, Gametype gametype) {

        for (QueueEntry queueEntry : list()) {
            if (!queueEntry.getGametype().equals(gametype) || !queueEntry.getQueuetype().equals(queuetype)) {
                continue;
            }

            return queueEntry;
        }

        QueueEntry queueEntry = new QueueEntry(queuetype, gametype);
        register(queueEntry);
        return queueEntry;
    }

    public static void remove(QueueEntry queueEntry) {
        list.remove(queueEntry);
    }

    public static List<QueueEntry> list() {
        return list;
    }
}
