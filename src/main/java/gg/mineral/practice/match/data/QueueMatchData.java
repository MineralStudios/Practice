package gg.mineral.practice.match.data;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.queue.QueueEntry;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;

import java.util.function.Supplier;

public class QueueMatchData extends MatchData {
    @Getter
    @NonNull
    private QueueEntry queueEntry;
    @Getter
    private boolean ranked = false;

    public QueueMatchData(QueueEntry queueEntry, Object2BooleanOpenHashMap<Arena> enabledArenas) {
        if (!enabledArenas.isEmpty())
            this.enabledArenas.putAll(this.enabledArenas);

        Thread.dumpStack();
        System.out.println("Enabled arenas: " + enabledArenas);

        setQueueEntry(queueEntry);
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
