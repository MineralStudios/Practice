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
    private byte teamSize = 1, playerBots = 0, opponentBots = 0;
    private boolean botQueue = false, arenaSelection = true;
    protected final Byte2BooleanOpenHashMap enabledArenas = new Byte2BooleanOpenHashMap();
    private int[] teamDifficulties = new int[playerBots],
            opponentDifficulties = new int[opponentBots];
    @Setter
    private BotConfiguration customBotConfiguration = Difficulty.EASY.getConfiguration(null);

    public void setEnabledArenas(Collection<Arena> enabledArenas) {
        for (Arena arena : enabledArenas)
            this.enabledArenas.put(arena.getId(), true);
    }

    public void setTeamSize(byte teamSize) {
        if (teamSize < 1 || teamSize > 16)
            throw new IllegalArgumentException("Team size must be between 1 and 16");
        this.teamSize = teamSize;

        if (playerBots >= teamSize)
            setPlayerBots((byte) (teamSize - 1));

        if (opponentBots > teamSize)
            setOpponentBots(teamSize);
    }

    public void setTeamDifficulties(Difficulty[] teamDifficulties) {
        if (teamDifficulties.length != playerBots)
            throw new IllegalArgumentException("Team difficulties must have the same length as player bots");

        for (int i = 0; i < teamDifficulties.length; i++)
            this.teamDifficulties[i] = teamDifficulties[i].ordinal();
    }

    public void setOpponentDifficulties(Difficulty[] opponentDifficulties) {
        if (opponentDifficulties.length != opponentBots)
            throw new IllegalArgumentException("Opponent difficulties must have the same length as opponent bots");

        for (int i = 0; i < opponentDifficulties.length; i++)
            this.opponentDifficulties[i] = opponentDifficulties[i].ordinal();
    }

    public void setTeamDifficulty(int index, Difficulty difficulty) {
        if (index < 0 || index >= playerBots)
            throw new IllegalArgumentException("Index must be between 0 and player bots - 1");
        teamDifficulties[index] = difficulty.ordinal();
    }

    public void setOpponentDifficulty(int index, Difficulty difficulty) {
        if (index < 0 || index >= opponentBots)
            throw new IllegalArgumentException("Index must be between 0 and opponent bots - 1");
        opponentDifficulties[index] = difficulty.ordinal();
    }

    public void setPlayerBots(byte playerBots) {
        if (playerBots < 0 || playerBots >= teamSize)
            throw new IllegalArgumentException("Player bots must be between 0 and team size - 1");
        if (this.playerBots == playerBots)
            return;
        this.playerBots = playerBots;
        this.teamDifficulties = new int[playerBots];
    }

    public void setOpponentBots(byte opponentBots) {
        if (opponentBots < 0 || opponentBots > teamSize)
            throw new IllegalArgumentException("Opponent bots must be between 0 and team size");

        if (this.opponentBots == opponentBots)
            return;
        this.opponentBots = opponentBots;
        this.opponentDifficulties = new int[opponentBots];
    }

    public static UUID toUUID(Queuetype queuetype, Gametype gametype, int teamSize, int playerBots, int opponentBots,
            Byte2BooleanOpenHashMap enabledArenas) {
        assert teamSize <= 16;
        assert playerBots <= teamSize - 1;
        assert opponentBots <= teamSize;
        long mostSigBits = 0L;
        byte queuetypeId = queuetype.getId();
        byte gametypeId = gametype.getId();

        // Convert values to longs and mask to ensure they are treated as unsigned
        // values
        long queueTypeLong = queuetypeId & 0xFFL;
        long gameTypeLong = gametypeId & 0xFFL;
        long teamSizeLong = teamSize & 0xFFL; // Ensure teamSize fits in 8 bits (0-255)
        long playerBotsLong = playerBots & 0xFL; // Mask to 4 bits (0-15)
        long opponentBotsLong = opponentBots & 0xFL; // Mask to 4 bits (0-15)

        // Store values in mostSigBits
        mostSigBits |= queueTypeLong; // bits 0-7
        mostSigBits |= (gameTypeLong << 8); // bits 8-15
        mostSigBits |= (teamSizeLong << 16); // bits 16-23
        mostSigBits |= (playerBotsLong << 24); // bits 24-27
        mostSigBits |= (opponentBotsLong << 28); // bits 28-31

        // Arenas
        long leastSigBits = 0L;

        for (it.unimi.dsi.fastutil.bytes.Byte2BooleanMap.Entry e : enabledArenas.byte2BooleanEntrySet()) {
            byte id = e.getByteKey();
            boolean enabled = e.getBooleanValue();
            int index = id & 0xFF; // Convert byte to unsigned int

            if (index < 0 || index > 63)
                throw new IllegalArgumentException("ID out of range: " + index);

            if (enabled)
                leastSigBits |= (1L << index); // Set the bit at 'index'
            else
                leastSigBits &= ~(1L << index); // Clear the bit at 'index'
        }

        return new UUID(mostSigBits, leastSigBits);
    }

    public static byte getQueueTypeId(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        return (byte) (mostSigBits & 0xFFL);
    }

    public static byte getGameTypeId(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        return (byte) ((mostSigBits >> 8) & 0xFFL);
    }

    public static int getTeamSize(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        return (int) ((mostSigBits >> 16) & 0xFFL);
    }

    public static int getPlayerBots(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        return (int) ((mostSigBits >> 24) & 0xFL);
    }

    public static int getOpponentBots(UUID uuid) {
        long mostSigBits = uuid.getMostSignificantBits();
        return (int) ((mostSigBits >> 28) & 0xFL);
    }

    public static boolean isArenaEnabled(UUID uuid, byte arenaId) {
        long leastSigBits = uuid.getLeastSignificantBits();
        int index = arenaId & 0xFF; // Convert byte to unsigned int (0 to 255)

        if (index < 0 || index > 63)
            throw new IllegalArgumentException("Arena ID must be between 0 and 63 inclusive.");

        // Check if the bit at position 'index' is set
        return (leastSigBits & (1L << index)) != 0;
    }

    public static boolean isCompatible(UUID uuid1, UUID uuid2) {
        // Extract data from UUIDs
        byte queueTypeId1 = getQueueTypeId(uuid1);
        byte gameTypeId1 = getGameTypeId(uuid1);
        int teamSize1 = getTeamSize(uuid1);
        int playerBots1 = getPlayerBots(uuid1);
        int opponentBots1 = getOpponentBots(uuid1);

        byte queueTypeId2 = getQueueTypeId(uuid2);
        byte gameTypeId2 = getGameTypeId(uuid2);
        int teamSize2 = getTeamSize(uuid2);
        int playerBots2 = getPlayerBots(uuid2);
        int opponentBots2 = getOpponentBots(uuid2);

        // Check if the queue type, game type, and team size are the same
        if (queueTypeId1 != queueTypeId2 || gameTypeId1 != gameTypeId2 || teamSize1 != teamSize2)
            return false;

        // Check if bot counts are compatible
        // The player's playerBots should match other's opponentBots, and vice versa
        if (playerBots1 != opponentBots2 || opponentBots1 != playerBots2)
            return false;

        // Check for at least one common enabled arena
        long leastSigBits1 = uuid1.getLeastSignificantBits();
        long leastSigBits2 = uuid2.getLeastSignificantBits();

        return (leastSigBits1 & leastSigBits2) != 0;
    }

    public Difficulty getBotDifficulty() {
        return Difficulty.values()[teamDifficulties[0]];
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
