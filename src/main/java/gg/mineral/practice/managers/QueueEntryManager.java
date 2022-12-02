package gg.mineral.practice.managers;

import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.api.collection.GlueList;

public class QueueEntryManager {
    static GlueList<QueueEntry> list = new GlueList<>();

    public static void register(QueueEntry qe) {
        list.add(qe);
    }

    public static QueueEntry newEntry(Queuetype q, Gametype g) {
        QueueEntry entry = null;

        for (QueueEntry qe : list) {
            if (qe.getGametype().equals(g) && qe.getQueuetype().equals(q)) {
                entry = qe;
                break;
            }
        }

        if (entry == null) {
            entry = new QueueEntry(q, g);
            register(entry);
        }

        return entry;
    }

    public void remove(QueueEntry qe) {
        list.remove(qe);
    }

    public GlueList<QueueEntry> getEntries() {
        return list;
    }
}
