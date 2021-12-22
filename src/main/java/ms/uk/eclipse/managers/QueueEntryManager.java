package ms.uk.eclipse.managers;

import land.strafe.api.collection.GlueList;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.queue.QueueEntry;
import ms.uk.eclipse.queue.Queuetype;

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
