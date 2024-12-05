package gg.mineral.practice.queue;

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
import lombok.val;

@Getter
@Setter
@RequiredArgsConstructor
public class QueueSettings {
    private byte teamSize = 1, opponentDifficulty = 0, teammateDifficulty = 0;
    private boolean botQueue = false, arenaSelection = true, oldCombat = false;
    protected final Byte2BooleanOpenHashMap enabledArenas = new Byte2BooleanOpenHashMap();
    private BotTeamSetting botTeamSetting = BotTeamSetting.BOTH;
    @Setter
    private BotConfiguration customBotConfiguration = Difficulty.EASY.getConfiguration(null);

    public static enum BotTeamSetting {
        BOTH, OPPONENT,
    }

    public record QueueEntry(Queuetype queuetype, Gametype gametype, int teamSize, boolean botsEnabled,
            boolean oldCombat,
            byte opponentDifficulty,
            BotTeamSetting botTeamSetting,
            Byte2BooleanOpenHashMap enabledArenas) {

        public boolean isCompatible(QueueEntry entry) {
            return queuetype == entry.queuetype && gametype == entry.gametype && teamSize == entry.teamSize
                    && botsEnabled == entry.botsEnabled && oldCombat == entry.oldCombat
                    && (teamSize > 1 && botsEnabled && entry.botsEnabled ? botTeamSetting == entry.botTeamSetting
                            : true)
                    && (botsEnabled ? opponentDifficulty == entry.opponentDifficulty : true)
                    && (enabledArenas.isEmpty() || entry.enabledArenas.isEmpty() || enabledArenas.byte2BooleanEntrySet()
                            .stream()
                            .anyMatch(arena -> entry.enabledArenas.get(arena.getByteKey()) == arena.getBooleanValue()));
        }
    }

    public static QueueEntry toEntry(Queuetype queuetype, Gametype gametype, int teamSize, boolean botsEnabled,
            boolean oldCombat,
            byte opponentDifficulty,
            BotTeamSetting bot2v2Setting,
            Byte2BooleanOpenHashMap enabledArenas) {
        return new QueueEntry(queuetype, gametype, teamSize,
                queuetype.isBotsEnabled() && gametype.isBotsEnabled() && botsEnabled, oldCombat,
                opponentDifficulty, bot2v2Setting,
                enabledArenas);
    }

    public Difficulty getOpponentBotDifficulty() {
        return Difficulty.values()[opponentDifficulty];
    }

    public Difficulty getTeammateBotDifficulty() {
        return Difficulty.values()[teammateDifficulty];
    }

    public void enableArena(Arena arena, boolean enabled) {
        enableArena(arena.getId(), enabled);
    }

    public void enableArena(byte id, boolean enabled) {
        this.enabledArenas.put(id, enabled);
    }

    public static ByteSet getEnabledArenaIds(UUID uuid) {
        val leastSigBits = uuid.getLeastSignificantBits();
        val enabledArenaIds = new ByteOpenHashSet();

        for (int index = 0; index < 64; index++)
            if ((leastSigBits & (1L << index)) != 0)
                enabledArenaIds.add((byte) index);

        return enabledArenaIds;
    }
}
