package gg.mineral.practice.match.data;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.queue.QueueEntry;
import lombok.Getter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;

public class QueueMatchData extends MatchData {
    @Getter
    @NonNull
    private QueueEntry queueEntry;
    @Getter
    private boolean ranked = false;

    public QueueMatchData(QueueEntry queueEntry) {
        setQueueEntry(queueEntry);
    }

    public QueueMatchData(QueueEntry queueEntry, ConcurrentLinkedQueue<Arena> enabledArenas) {
        this(queueEntry);
        setEnabledArenas(enabledArenas);
    }

    public QueueMatchData setQueueEntry(@Nullable QueueEntry queueEntry) {
        this.queueEntry = queueEntry;
        setGametype(queueEntry.getGametype());
        knockback = queueEntry.getQueuetype().getKnockback();
        arena = queueEntry.getQueuetype().nextArena(this, this.gametype);
        ranked = queueEntry.getQueuetype().isRanked();
        return this;
    }

    @Override
    public <D extends MatchData> D newClone(Supplier<D> supplier) {
        D data = super.newClone(supplier);
        if (data instanceof QueueMatchData qData) {
            qData.queueEntry = this.queueEntry;
            qData.ranked = this.ranked;
        }
        return data;
    }
}
