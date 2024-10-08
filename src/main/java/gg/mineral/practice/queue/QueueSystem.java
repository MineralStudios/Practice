package gg.mineral.practice.queue;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class QueueSystem {

    // Map of queueEntryUUID to list of players waiting with that queue entry
    private static final Map<UUID, List<QueuedEntity>> queueMap = new ConcurrentHashMap<>();

    // Synchronization lock
    private static final Object lock = new Object();

    // Method to add a player to the queue
    public static void addPlayerToQueue(QueuedEntity queueEntity, UUID queueEntryUUID) {
        synchronized (lock) {
            // Add player to the queue map
            queueMap.computeIfAbsent(queueEntryUUID, k -> new GlueList<>()).add(queueEntity);

            // Retrieve teamSize and bot counts
            int teamSize = QueueSettings.getTeamSize(queueEntryUUID);
            int playerBots = QueueSettings.getPlayerBots(queueEntryUUID);
            int opponentBots = QueueSettings.getOpponentBots(queueEntryUUID);

            // Try to find a match
            findMatchForQueueEntry(queueEntryUUID, teamSize, playerBots, opponentBots);
        }
    }

    public static List<UUID> getQueueEntries(QueuedEntity entity, Queuetype queuetype, Gametype gametype) {
        List<UUID> queueEntries = new GlueList<>();
        synchronized (lock) {
            for (Entry<UUID, List<QueuedEntity>> entry : queueMap.entrySet())
                if (entry.getValue().contains(entity))
                    if (QueueSettings.getQueueTypeId(entry.getKey()) == queuetype.getId()
                            && QueueSettings.getGameTypeId(entry.getKey()) == gametype.getId())
                        queueEntries.add(entry.getKey());
        }

        return queueEntries;
    }

    public static List<UUID> getQueueEntries(QueuedEntity entity) {
        List<UUID> queueEntries = new GlueList<>();
        synchronized (lock) {
            for (Entry<UUID, List<QueuedEntity>> entry : queueMap.entrySet())
                if (entry.getValue().contains(entity))
                    queueEntries.add(entry.getKey());
        }

        return queueEntries;
    }

    // Method to find a match for a specific queue entry
    private static void findMatchForQueueEntry(UUID queueEntryUUID, int teamSize, int playerBots, int opponentBots) {
        // Get the list of players with this queue entry
        List<QueuedEntity> entitiesWithEntry = queueMap.get(queueEntryUUID);

        // Total number of human players needed for both teams
        int playersTeamHumanNeeded = teamSize - playerBots;
        int opponentsTeamHumanNeeded = teamSize - opponentBots;

        int playerCount = 0;

        for (QueuedEntity entity : entitiesWithEntry)
            playerCount += entity.getProfiles().size();

        // Check if we have enough players with the same queue entry
        if (playerCount >= playersTeamHumanNeeded + opponentsTeamHumanNeeded) {
            // We have enough players to form both teams
            // Proceed to form teams

            List<QueuedEntity> matchPlayers = new ArrayList<>(
                    entitiesWithEntry.subList(0, playersTeamHumanNeeded + opponentsTeamHumanNeeded));

            // Create a map to track each player's queue entry UUID
            Map<QueuedEntity, UUID> playerQueueMap = new HashMap<>();
            for (QueuedEntity player : matchPlayers)
                playerQueueMap.put(player, queueEntryUUID);

            // Remove these players from the queue
            entitiesWithEntry.subList(0, playersTeamHumanNeeded + opponentsTeamHumanNeeded).clear();
            if (entitiesWithEntry.isEmpty())
                queueMap.remove(queueEntryUUID);

            // Start the match
            startMatch(playerQueueMap, teamSize, playerBots, opponentBots);

            return;
        }

        // Otherwise, look for compatible queue entries
        for (Map.Entry<UUID, List<QueuedEntity>> entry : queueMap.entrySet()) {
            UUID otherUUID = entry.getKey();
            if (!otherUUID.equals(queueEntryUUID)) {
                // Check compatibility
                if (QueueSettings.isCompatible(queueEntryUUID, otherUUID)) {
                    // Retrieve other player's bot counts
                    int otherTeamSize = QueueSettings.getTeamSize(otherUUID);
                    int otherPlayerBots = QueueSettings.getPlayerBots(otherUUID);
                    int otherOpponentBots = QueueSettings.getOpponentBots(otherUUID);

                    // Ensure bot counts are compatible
                    if (teamSize != otherTeamSize || playerBots != otherOpponentBots
                            || opponentBots != otherPlayerBots)
                        continue;

                    List<QueuedEntity> otherEntities = entry.getValue();

                    int totalPlayersNeeded = playersTeamHumanNeeded + opponentsTeamHumanNeeded;

                    int totalAvailablePlayers = 0;

                    for (QueuedEntity entity : entitiesWithEntry)
                        totalAvailablePlayers += entity.getProfiles().size();

                    for (QueuedEntity entity : otherEntities)
                        totalAvailablePlayers += entity.getProfiles().size();

                    if (totalAvailablePlayers >= totalPlayersNeeded) {
                        // We have enough compatible players
                        List<QueuedEntity> combinedEntities = new ArrayList<>();
                        combinedEntities.addAll(entitiesWithEntry);
                        combinedEntities.addAll(otherEntities);

                        // Select the required number of players
                        List<QueuedEntity> matchEntities = combinedEntities.subList(0, totalPlayersNeeded);

                        // Create a map to track each player's queue entry UUID
                        Map<QueuedEntity, UUID> entityQueueMap = new HashMap<>();
                        for (QueuedEntity entity : matchEntities) {
                            if (entitiesWithEntry.contains(entity))
                                entityQueueMap.put(entity, queueEntryUUID);
                            else if (otherEntities.contains(entity))
                                entityQueueMap.put(entity, otherUUID);
                        }

                        // Remove these players from their respective queues
                        removePlayersFromQueue(entityQueueMap);

                        // Start the match
                        startMatch(entityQueueMap, teamSize, playerBots, opponentBots);

                        break;
                    }
                }
            }
        }
    }

    private static void removePlayersFromQueue(Map<QueuedEntity, UUID> playerQueueMap) {
        synchronized (lock) {
            for (Map.Entry<QueuedEntity, UUID> entry : playerQueueMap.entrySet()) {
                QueuedEntity player = entry.getKey();
                UUID queueEntryUUID = entry.getValue();
                List<QueuedEntity> playersInQueue = queueMap.get(queueEntryUUID);
                if (playersInQueue != null) {
                    playersInQueue.remove(player);
                    if (playersInQueue.isEmpty())
                        queueMap.remove(queueEntryUUID);
                }
            }
        }
    }

    public static boolean removePlayerFromQueue(QueuedEntity entity, Queuetype queuetype, Gametype gametype) {
        boolean entityStillPresent = false;
        synchronized (lock) {
            for (Entry<UUID, List<QueuedEntity>> e : queueMap.entrySet()) {
                UUID queueEntryUUID = e.getKey();
                if (QueueSettings.getQueueTypeId(queueEntryUUID) == queuetype.getId()
                        && QueueSettings.getGameTypeId(queueEntryUUID) == gametype.getId()) {
                    List<QueuedEntity> entities = e.getValue();
                    entities.remove(entity);
                    if (entities.isEmpty())
                        queueMap.remove(queueEntryUUID);
                }
            }
        }
        return entityStillPresent;
    }

    public static void removePlayerFromQueue(QueuedEntity player) {
        synchronized (lock) {
            Iterator<Entry<UUID, List<QueuedEntity>>> iterator = queueMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<UUID, List<QueuedEntity>> e = iterator.next();
                List<QueuedEntity> players = e.getValue();
                if (players.remove(player))
                    if (players.isEmpty())
                        iterator.remove();
            }
        }
    }

    // Method to start a match with the given players
    private static void startMatch(Map<QueuedEntity, UUID> playerQueueMap, int teamSize, int playerBots,
            int opponentBots) {
        // First, find common arenas
        Set<UUID> queueEntryUUIDs = new ObjectOpenHashSet<>(playerQueueMap.values());
        Map<UUID, ByteSet> uuidArenaIdsMap = new Object2ObjectOpenHashMap<>();
        for (UUID uuid : queueEntryUUIDs) {
            ByteSet arenaIds = QueueSettings.getEnabledArenaIds(uuid);
            uuidArenaIdsMap.put(uuid, arenaIds);
        }

        ByteSet commonArenaIds = null;
        for (ByteSet arenaIds : uuidArenaIdsMap.values()) {
            if (commonArenaIds == null)
                commonArenaIds = new ByteOpenHashSet(arenaIds);
            else
                commonArenaIds.retainAll(arenaIds);
        }

        if (commonArenaIds == null || commonArenaIds.isEmpty())
            throw new IllegalStateException("No common arenas found for queue entries: " + queueEntryUUIDs);

        // Select one arena from commonArenaIds
        byte selectedArenaId = commonArenaIds.iterator().nextByte();

        // Get the Arena object from the arena ID
        Arena selectedArena = ArenaManager.getArenas()[selectedArenaId];
        if (selectedArena == null)
            throw new IllegalStateException("Arena not found for ID: " + selectedArenaId);

        // Now proceed to form teams as before
        List<QueuedEntity> players = new GlueList<>(playerQueueMap.keySet());

        // Split players into two teams, accounting for bots
        int playersTeamHumanCount = teamSize - playerBots;
        int opponentsTeamHumanCount = teamSize - opponentBots;

        List<Profile> teamA = new GlueList<>(), teamB = new GlueList<>();
        List<BotConfiguration> teamABots = new GlueList<>(), teamBBots = new GlueList<>();

        // Assign remaining players to teams
        for (QueuedEntity profile : players) {
            int size = profile.getProfiles().size();
            if (teamA.size() + size <= playersTeamHumanCount)
                teamA.addAll(profile.getProfiles());
            else
                teamB.addAll(profile.getProfiles());
        }

        assert teamA.size() == playersTeamHumanCount;
        assert teamB.size() == opponentsTeamHumanCount;

        // Add bots to teams
        for (int i = 0; i < playerBots; i++)
            teamABots.add(Difficulty.RANDOM.getConfiguration(null));

        for (int i = 0; i < opponentBots; i++)
            teamBBots.add(Difficulty.RANDOM.getConfiguration(null));

        MatchData matchData = new MatchData(queueEntryUUIDs.toArray(new UUID[0])[0]);

        // Start the match, passing in the selectedArena
        if (teamSize == 1 && opponentBots == 0)
            new Match(teamA.get(0), teamB.get(0), matchData).start();
        else
            new BotTeamMatch(teamA, teamB, teamABots, teamBBots, matchData).start();
    }

    public static int getCompatibleQueueCount(UUID queueEntry) {
        int count = 0;
        synchronized (lock) {
            for (Entry<UUID, List<QueuedEntity>> e : queueMap.entrySet())
                if (QueueSettings.isCompatible(queueEntry, e.getKey()))
                    count += e.getValue().size();
        }

        return count;
    }
}
