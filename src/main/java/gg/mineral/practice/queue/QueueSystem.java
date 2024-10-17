package gg.mineral.practice.queue;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.match.BotTeamMatch;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.queue.QueueSettings.QueueEntry;
import it.unimi.dsi.fastutil.bytes.Byte2BooleanMap;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

public class QueueSystem {

    private static final Short2ObjectOpenHashMap<RecordSet> queueMap = new Short2ObjectOpenHashMap<>();

    private static class RecordSet extends ObjectOpenHashSet<QueueRecord> {
        private static final long serialVersionUID = 1L;

        public int playerCount() {
            int count = 0;
            for (QueueRecord queueRecord : this)
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
            QueueRecord other = (QueueRecord) obj;
            return entity.equals(other.entity);
        }

        public boolean isCompatible(QueueRecord other) {
            return !equals(other) && queueEntry.isCompatible(other.queueEntry());
        }
    }

    public static void addPlayerToQueue(QueuedEntity queueEntity, QueueEntry queueEntry) {
        short queueAndGametypeHash = (short) (queueEntry.queuetype().getId() << 8 | queueEntry.gametype().getId());
        QueueRecord queueRecord = new QueueRecord(queueEntity, queueEntry);

        if (!findMatch(queueAndGametypeHash, queueRecord))
            queueMap.computeIfAbsent(queueAndGametypeHash, k -> new RecordSet())
                    .add(queueRecord);
    }

    private static boolean findMatch(short queueAndGametypeHash, QueueRecord queueRecord) {
        Set<QueueRecord> queueRecords = queueMap.get(queueAndGametypeHash);
        Set<QueueRecord> compatibleQueueRecords = new ObjectOpenHashSet<>();

        for (QueueRecord record : queueRecords)
            if (queueRecord.isCompatible(record))
                compatibleQueueRecords.add(record);

        if (compatibleQueueRecords.isEmpty())
            return false;

        int teamSize = queueRecord.queueEntry().teamSize();
        Set<QueueRecord> teammateBot = new ObjectOpenHashSet<>(), opponentBot = new ObjectOpenHashSet<>();

        for (QueueRecord record : compatibleQueueRecords) {
            if (record.queueEntry().teammateBot())
                teammateBot.add(record);
            else if (record.queueEntry().opponentBot())
                opponentBot.add(record);
        }

        // teamA is a bot team with 1 player and the rest are bots
        for (QueueRecord teamARecord : teammateBot) {
            RecordSet team1 = new RecordSet(), team2 = new RecordSet();
            team1.add(teamARecord);

            Iterator<QueueRecord> iterator = opponentBot.iterator();

            while (iterator.hasNext() && team2.playerCount() < teamSize) {
                QueueRecord teamBRecord = iterator.next();
                team2.add(teamBRecord);
            }

            if (team2.playerCount() < teamSize)
                continue;

            startMatch(queueRecord, team1, team2,
                    teamSize, queueRecord.queueEntry().teammateBot(),
                    queueRecord.queueEntry().opponentBot());
            queueRecords.removeAll(team1);
            queueRecords.removeAll(team2);
            return true;
        }

        if (teammateBot.size() + opponentBot.size() == 0) {
            RecordSet team1 = new RecordSet(), team2 = new RecordSet();

            Iterator<QueueRecord> iterator = compatibleQueueRecords.iterator();

            while (iterator.hasNext() && team2.playerCount() < teamSize) {
                QueueRecord record = iterator.next();
                if (team1.playerCount() < teamSize)
                    team1.add(record);
                else
                    team2.add(record);
            }

            startMatch(queueRecord, team1, team2,
                    teamSize, queueRecord.queueEntry().teammateBot(),
                    queueRecord.queueEntry().opponentBot());
            queueRecords.removeAll(team1);
            queueRecords.removeAll(team2);
            return true;
        }

        return false;
    }

    @Nullable
    public static QueueEntry getQueueEntry(QueuedEntity entity, Queuetype queuetype, Gametype gametype) {
        short queueAndGametypeHash = (short) (queuetype.getId() << 8 | gametype.getId());

        Set<QueueRecord> queueRecords = queueMap.get(queueAndGametypeHash);

        if (queueRecords != null)
            for (QueueRecord record : queueRecords)
                if (record.entity().equals(entity))
                    return record.queueEntry();

        return null;
    }

    public static List<QueueEntry> getQueueEntries(QueuedEntity entity) {
        List<QueueEntry> queueEntries = new GlueList<>();

        for (RecordSet recordSet : queueMap.values())
            for (QueueRecord record : recordSet)
                if (record.entity().equals(entity))
                    queueEntries.add(record.queueEntry());

        return queueEntries;
    }

    public static boolean removePlayerFromQueue(QueuedEntity entity, Queuetype queuetype, Gametype gametype) {
        short queueAndGametypeHash = (short) (queuetype.getId() << 8 | gametype.getId());

        Set<QueueRecord> queueRecords = queueMap.get(queueAndGametypeHash);

        queueRecords.removeIf(record -> record.entity().equals(entity));
        return queueMap.values().stream()
                .anyMatch(set -> set.stream().anyMatch(record -> record.entity().equals(entity)));
    }

    public static void removePlayerFromQueue(QueuedEntity player) {
        for (Set<QueueRecord> queueRecords : queueMap.values())
            queueRecords.removeIf(record -> record.entity().equals(player));
    }

    private static void startMatch(QueueRecord sampleRecord, Set<QueueRecord> team1,
            Set<QueueRecord> team2, int teamSize, boolean teammateBot,
            boolean opponentBot) {
        // First, find common arenas
        ByteSet allEnabledArenas = new ByteOpenHashSet(), allDisabledArenas = new ByteOpenHashSet();
        for (QueueRecord record : team1)
            for (Byte2BooleanMap.Entry e : record.queueEntry().enabledArenas().byte2BooleanEntrySet())
                if (e.getBooleanValue())
                    allEnabledArenas.add(e.getByteKey());
                else
                    allDisabledArenas.add(e.getByteKey());

        for (QueueRecord record : team2)
            for (Byte2BooleanMap.Entry e : record.queueEntry().enabledArenas().byte2BooleanEntrySet())
                if (e.getBooleanValue())
                    allEnabledArenas.add(e.getByteKey());
                else
                    allDisabledArenas.add(e.getByteKey());

        allEnabledArenas.removeAll(allDisabledArenas);

        ByteSet commonArenaIds = allEnabledArenas;

        @Nullable
        byte selectedArenaId;

        if (commonArenaIds == null || commonArenaIds.isEmpty())
            selectedArenaId = sampleRecord.queueEntry().queuetype().nextArenaId(sampleRecord.queueEntry().gametype());
        else
            selectedArenaId = commonArenaIds.iterator().nextByte();

        // Get the Arena object from the arena ID
        Arena selectedArena = ArenaManager.getArenas().get(selectedArenaId);
        if (selectedArena == null)
            throw new IllegalStateException("Arena not found for ID: " + selectedArenaId);

        List<Profile> teamA = new GlueList<>(), teamB = new GlueList<>();
        List<BotConfiguration> teamABots = new GlueList<>(), teamBBots = new GlueList<>();

        for (QueueRecord record : team1)
            teamA.addAll(record.entity().getProfiles());

        for (QueueRecord record : team2)
            teamB.addAll(record.entity().getProfiles());

        // Add bots to teams
        while (teamA.size() + teamABots.size() < teamSize)
            teamABots.add(Difficulty.RANDOM.getConfiguration(null));

        while (teamB.size() + teamBBots.size() < teamSize)
            teamBBots.add(Difficulty.RANDOM.getConfiguration(null));

        MatchData matchData = new MatchData(sampleRecord.queueEntry());

        // Start the match, passing in the selectedArena
        if (teamSize == 1 && !sampleRecord.queueEntry().teammateBot() && !sampleRecord.queueEntry().opponentBot())
            new Match(teamA.get(0), teamB.get(0), matchData).start();
        else
            new BotTeamMatch(teamA, teamB, teamABots, teamBBots, matchData).start();
    }

    public static int getCompatibleQueueCount(Queuetype queuetype, Gametype gametype) {
        short queueAndGametypeHash = (short) (queuetype.getId() << 8 | gametype.getId());
        return queueMap.computeIfAbsent(queueAndGametypeHash, k -> new RecordSet()).size();
    }
}
