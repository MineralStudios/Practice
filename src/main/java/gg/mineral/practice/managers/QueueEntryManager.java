package gg.mineral.practice.managers;

import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.Queuetype;
import land.strafe.api.collection.GlueList;

public class QueueEntryManager {
    GlueList<QueueEntry> list = new GlueList<>();

    public void register(QueueEntry qe) {
        list.add(qe);
    }

    public QueueEntry newEntry(Queuetype q, Gametype g) {
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
