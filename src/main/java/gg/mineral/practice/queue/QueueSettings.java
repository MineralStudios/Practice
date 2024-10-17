package gg.mineral.practice.queue;

import java.util.Collection;
import java.util.UUID;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.gametype.Gametype;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class QueueSettings {
    private byte teamSize = 1, difficulty = 0;
    private boolean botQueue = false, arenaSelection = true;
    protected final Byte2BooleanOpenHashMap enabledArenas = new Byte2BooleanOpenHashMap();
    private boolean teammateBot, opponentBot;
    @Setter
    private BotConfiguration customBotConfiguration = Difficulty.EASY.getConfiguration(null);

    public record QueueEntry(Queuetype queuetype, Gametype gametype, int teamSize, boolean teammateBot,
            boolean opponentBot,
            Byte2BooleanOpenHashMap enabledArenas) {

        public boolean isCompatible(QueueEntry entry) {
            return queuetype == entry.queuetype && gametype == entry.gametype && teamSize == entry.teamSize
                    && (teammateBot == entry.teammateBot && opponentBot == entry.opponentBot
                            || teammateBot == entry.opponentBot && opponentBot == entry.teammateBot)
                    && enabledArenas.byte2BooleanEntrySet().stream()
                            .anyMatch(arena -> entry.enabledArenas.get(arena.getByteKey()) == arena.getBooleanValue());
        }
    }

    public void setEnabledArenas(Collection<Arena> enabledArenas) {
        for (Arena arena : enabledArenas)
            this.enabledArenas.put(arena.getId(), true);
    }

    public static QueueEntry toEntry(Queuetype queuetype, Gametype gametype, int teamSize, boolean teammateBot,
            boolean opponentBot,
            Byte2BooleanOpenHashMap enabledArenas) {
        return new QueueEntry(queuetype, gametype, teamSize, teammateBot, opponentBot, enabledArenas);
    }

    public Difficulty getBotDifficulty() {
        return Difficulty.values()[difficulty];
    }

    public void enableArena(Arena arena, boolean enabled) {
        enableArena(arena.getId(), enabled);
    }

    public void enableArena(byte id, boolean enabled) {
        this.enabledArenas.put(id, enabled);
    }

    public static ByteSet getEnabledArenaIds(UUID uuid) {
        long leastSigBits = uuid.getLeastSignificantBits();
        ByteSet enabledArenaIds = new ByteOpenHashSet();

        for (int index = 0; index < 64; index++)
            if ((leastSigBits & (1L << index)) != 0)
                enabledArenaIds.add((byte) index);

        return enabledArenaIds;
    }
}
