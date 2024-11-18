package gg.mineral.practice.queue;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.api.configuration.BotConfiguration;

import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.match.BotMatch;
import gg.mineral.practice.match.BotTeamMatch;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.TeamMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueSettings.QueueEntry;

import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.val;

public class QueueSystem {

    private static final Short2ObjectOpenHashMap<RecordSet> queueMap = new Short2ObjectOpenHashMap<>();

    private static class RecordSet extends ObjectOpenHashSet<QueueRecord> {
        private static final long serialVersionUID = 1L;

        public int playerCount() {
            int count = 0;
            for (val queueRecord : this)
                count += queueRecord.entity().getProfiles().size();

            return count;
        }
    }

    private static record QueueRecord(QueuedEntity entity, QueueEntry queueEntry) {
        @Override
        public int hashCode() {
            return entity.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            val other = (QueueRecord) obj;
            return entity.equals(other.entity);
        }

        public boolean isCompatible(QueueRecord other) {
            return !equals(other) && queueEntry.isCompatible(other.queueEntry());
        }
    }

    public static boolean addPlayerToQueue(QueuedEntity queueEntity, QueueEntry queueEntry) {
        short queueAndGametypeHash = (short) (queueEntry.queuetype().getId() << 8 | queueEntry.gametype().getId());
        val queueRecord = new QueueRecord(queueEntity, queueEntry);

        boolean matchFound = findMatch(queueAndGametypeHash, queueRecord);

        if (!matchFound)
            queueMap.computeIfAbsent(queueAndGametypeHash, k -> new RecordSet())
                    .add(queueRecord);

        return matchFound;
    }

    private static boolean findMatch(short queueAndGametypeHash, QueueRecord queueRecord) {

        val bots = queueRecord.queueEntry.botsEnabled();
        val teamSize = queueRecord.queueEntry().teamSize();

        if (bots && teamSize == 1 && queueRecord.entity() instanceof Profile profile) {
            val entry = queueRecord.queueEntry();
            val data = new MatchData(queueRecord.queueEntry(), profile.getQueueSettings());
            val arenaId = entry.queuetype().nextArenaId(data, entry.gametype());
            data.setArenaId(arenaId);
            val queueSettings = profile.getQueueSettings();
            val botDifficulty = queueSettings.getOpponentBotDifficulty();
            val botConfiguration = botDifficulty.getConfiguration(queueSettings);
            new BotMatch(profile, botConfiguration, data).start();
            return true;
        }

        if (bots && teamSize == 1)
            throw new IllegalStateException("Bot 1v1s are not supported for non-profile entities");

        val botTeamSetting = queueRecord.queueEntry().botTeamSetting();
        if (bots
                && botTeamSetting == QueueSettings.BotTeamSetting.BOTH) {
            // TODO: make configurable team sizes for parties
            val entity = queueRecord.entity();
            val entry = queueRecord.queueEntry();
            val data = new MatchData(entry, entity.getQueueSettings());
            val arenaId = entry.queuetype().nextArenaId(data, entry.gametype());
            data.setArenaId(arenaId);
            val queueSettings = entity.getQueueSettings();
            val teamBotDifficulty = queueSettings.getTeammateBotDifficulty();
            val opponentBotDifficulty = queueSettings.getOpponentBotDifficulty();
            val teamBotsNeeded = teamSize - entity.getProfiles().size();
            val teamBots = new GlueList<BotConfiguration>(teamBotsNeeded);
            val opponentBots = new GlueList<BotConfiguration>(teamSize);
            for (int i = 0; i < teamBotsNeeded; i++)
                teamBots.add(teamBotDifficulty.getConfiguration(queueSettings));

            for (int i = 0; i < teamSize; i++)
                opponentBots.add(opponentBotDifficulty.getConfiguration(queueSettings));

            new BotTeamMatch(entity.getProfiles(), new GlueList<>(), teamBots, opponentBots, data).start();
            return true;
        }

        if (teamSize > 1 && bots && botTeamSetting == QueueSettings.BotTeamSetting.BOTH)
            throw new IllegalStateException("An unknown error occurred while trying to find a match");

        val queueRecords = queueMap.get(queueAndGametypeHash);

        if (queueRecords == null)
            return false;

        val compatibleQueueRecords = new RecordSet();

        for (val record : queueRecords)
            if (queueRecord.isCompatible(record))
                compatibleQueueRecords.add(record);

        if (compatibleQueueRecords.isEmpty())
            return false;

        compatibleQueueRecords.add(queueRecord);

        val requiredPlayers = teamSize * (bots ? 1 : 2);
        val records = new RecordSet();

        val iter = compatibleQueueRecords.iterator();

        while (iter.hasNext()) {
            val record = iter.next();
            val profiles = record.entity().getProfiles();
            if (records.playerCount() + profiles.size() <= requiredPlayers)
                records.add(record);
        }

        if (records.playerCount() < requiredPlayers)
            return false;

        startMatch(queueRecord, records, teamSize, bots);
        return true;
    }

    @Nullable
    public static QueueEntry getQueueEntry(Profile profile, Queuetype queuetype, Gametype gametype) {
        return getQueueEntry(profile.isInParty() ? profile.getParty() : profile, queuetype, gametype);
    }

    @Nullable
    public static QueueEntry getQueueEntry(QueuedEntity entity, Queuetype queuetype, Gametype gametype) {
        short queueAndGametypeHash = (short) (queuetype.getId() << 8 | gametype.getId());

        val queueRecords = queueMap.get(queueAndGametypeHash);

        if (queueRecords != null)
            for (val record : queueRecords)
                if (record.entity().equals(entity))
                    return record.queueEntry();

        return null;
    }

    public static List<QueueEntry> getQueueEntries(Profile profile) {
        return getQueueEntries(profile.isInParty() ? profile.getParty() : profile);
    }

    public static List<QueueEntry> getQueueEntries(QueuedEntity entity) {
        val queueEntries = new GlueList<QueueEntry>();

        for (val recordSet : queueMap.values())
            for (val record : recordSet)
                if (record.entity().equals(entity))
                    queueEntries.add(record.queueEntry());

        return queueEntries;
    }

    public static boolean removePlayerFromQueue(Profile profile, Queuetype queuetype, Gametype gametype) {
        return removePlayerFromQueue(profile.isInParty() ? profile.getParty() : profile, queuetype, gametype);
    }

    public static boolean removePlayerFromQueue(QueuedEntity entity, Queuetype queuetype, Gametype gametype) {
        short queueAndGametypeHash = (short) (queuetype.getId() << 8 | gametype.getId());

        val queueRecords = queueMap.get(queueAndGametypeHash);

        queueRecords.removeIf(record -> record.entity().equals(entity));
        return queueMap.values().stream()
                .anyMatch(set -> set.stream().anyMatch(record -> record.entity().equals(entity)));
    }

    public static void removePlayerFromQueue(Profile profile) {
        removePlayerFromQueue(profile.isInParty() ? profile.getParty() : profile);
    }

    public static void removePlayerFromQueue(QueuedEntity player) {
        for (val queueRecords : queueMap.values())
            queueRecords.removeIf(record -> record.entity().equals(player));
    }

    private static void startMatch(QueueRecord sampleRecord, RecordSet records, int teamSize,
            boolean opponentBot) {
        // First, find common arenas
        val allEnabledArenas = new ByteOpenHashSet();
        val allDisabledArenas = new ByteOpenHashSet();
        for (val record : records) {
            val enabledArenas = record.queueEntry().enabledArenas();

            if (enabledArenas.isEmpty()) {
                allEnabledArenas
                        .addAll(record.queueEntry().queuetype().filterArenasByGametype(record.queueEntry().gametype()));
                continue;
            }

            for (val e : enabledArenas.byte2BooleanEntrySet())
                if (e.getBooleanValue())
                    allEnabledArenas.add(e.getByteKey());
                else
                    allDisabledArenas.add(e.getByteKey());
        }

        allEnabledArenas.removeAll(allDisabledArenas);

        val commonArenaIds = allEnabledArenas;

        val selectedArenaId = commonArenaIds.isEmpty()
                ? sampleRecord.queueEntry().queuetype().nextArenaId(sampleRecord.queueEntry().gametype())
                : commonArenaIds.iterator().nextByte();

        // Get the Arena object from the arena ID
        val selectedArena = ArenaManager.getArenas().get(selectedArenaId);
        if (selectedArena == null)
            throw new IllegalStateException("Arena not found for ID: " + selectedArenaId);

        val teamA = new GlueList<Profile>();
        val teamB = new GlueList<Profile>();

        val recordIter = records.iterator();

        while (recordIter.hasNext()) {
            val record = recordIter.next();
            val profiles = record.entity().getProfiles();
            if (teamA.size() + profiles.size() <= teamSize)
                teamA.addAll(profiles);
            else
                teamB.addAll(profiles);
        }

        val bots = sampleRecord.queueEntry().botsEnabled();
        val matchData = new MatchData(sampleRecord.queueEntry());
        matchData.setArenaId(selectedArenaId);

        val difficulty = Difficulty.values()[sampleRecord.queueEntry().opponentDifficulty()];

        if (bots) {
            teamA.addAll(teamB);
            val teamBBots = new GlueList<BotConfiguration>();
            for (int i = 0; i < teamSize; i++)
                teamBBots.add(difficulty.getConfiguration(null));
            new BotTeamMatch(teamA, new GlueList<>(), new GlueList<>(), teamBBots, matchData).start();
            return;
        }

        if (teamSize == 1)
            new Match(teamA.get(0), teamB.get(0), matchData).start();
        else
            new TeamMatch(teamA, teamB, matchData).start();
    }

    public static int getCompatibleQueueCount(Queuetype queuetype, Gametype gametype) {
        short queueAndGametypeHash = (short) (queuetype.getId() << 8 | gametype.getId());
        return queueMap.computeIfAbsent(queueAndGametypeHash, k -> new RecordSet()).size();
    }
}
