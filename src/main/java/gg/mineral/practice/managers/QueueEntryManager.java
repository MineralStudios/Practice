package gg.mineral.practice.managers;

import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.api.collection.GlueList;

public class QueueEntryManager {
    static GlueList<QueueEntry> entries = new GlueList<>();

    public static void register(QueueEntry queueEntry) {
        entries.add(queueEntry);
    }

    public static QueueEntry newEntry(Queuetype q, Gametype g) {
        QueueEntry entry = null;

        for (QueueEntry queueEntry : entries) {
            if (queueEntry.getGametype().equals(g) && queueEntry.getQueuetype().equals(q)) {
                entry = queueEntry;
                break;
            }
        }

        if (entry == null) {
            register(entry = new QueueEntry(q, g));
        }

        return entry;
    }
}
