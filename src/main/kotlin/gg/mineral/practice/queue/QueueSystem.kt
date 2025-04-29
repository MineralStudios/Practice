package gg.mineral.practice.queue

import gg.mineral.api.collection.GlueList
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.practice.bots.Difficulty
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.match.BotMatch
import gg.mineral.practice.match.BotTeamMatch
import gg.mineral.practice.match.Match
import gg.mineral.practice.match.TeamMatch
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.queue.QueueSettings.BotTeamSetting
import gg.mineral.practice.queue.QueueSettings.QueueEntry
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import java.util.*

object QueueSystem {
    private val random = Random()
    private val queueMap = Short2ObjectOpenHashMap<RecordSet>()

    fun addPlayerToQueue(queueEntity: QueuedEntity, queueEntry: QueueEntry): Boolean {
        val queueAndGametypeHash = queuetypeAndGametypeHash(queueEntry.queuetype, queueEntry.gametype)
        val queueRecord = QueueRecord(queueEntity, queueEntry)

        val matchFound = findMatch(queueAndGametypeHash, queueRecord)

        if (!matchFound) queueMap.computeIfAbsent(
            queueAndGametypeHash,
            Short2ObjectFunction { RecordSet() })
            .add(queueRecord)

        return matchFound
    }

    private fun findMatch(queueAndGametypeHash: Short, queueRecord: QueueRecord): Boolean {
        val bots = queueRecord.queueEntry.botsEnabled
        val teamSize = queueRecord.queueEntry.teamSize

        if (bots && teamSize == 1 && queueRecord.entity is Profile) {
            val profile = queueRecord.entity
            val entry = queueRecord.queueEntry
            val data = MatchData(queueRecord.queueEntry, profile.queueSettings)
            val arenaId = data.nextArenaIdFiltered(entry.compatibleArenas())
            data.arenaId = arenaId
            val queueSettings: QueueSettings = profile.queueSettings
            val botDifficulty = queueSettings.opponentBotDifficulty
            val botConfiguration = botDifficulty.getConfiguration(queueSettings)
            BotMatch(profile, botConfiguration, data).start()
            return true
        }

        check(!(bots && teamSize == 1)) { "Bot 1v1s are not supported for non-profile entities" }

        val botTeamSetting = queueRecord.queueEntry.botTeamSetting
        if (bots
            && botTeamSetting == BotTeamSetting.BOTH
        ) {
            // TODO: make configurable team sizes for parties
            val entity = queueRecord.entity
            val entry = queueRecord.queueEntry
            val data = MatchData(entry, entity.queueSettings)
            val arenaId = data.nextArenaIdFiltered(entry.compatibleArenas())
            data.arenaId = arenaId
            val queueSettings = entity.queueSettings
            val teamBotDifficulty = queueSettings.teammateBotDifficulty
            val opponentBotDifficulty = queueSettings.opponentBotDifficulty
            val teamBotsNeeded = teamSize - entity.profiles.size
            val teamBots: GlueList<BotConfiguration> = GlueList<BotConfiguration>(teamBotsNeeded)
            val opponentBots = GlueList<BotConfiguration>(teamSize)
            for (i in 0..<teamBotsNeeded) teamBots.add(teamBotDifficulty.getConfiguration(queueSettings))
            for (i in 0..<teamSize) opponentBots.add(opponentBotDifficulty.getConfiguration(queueSettings))
            return BotTeamMatch(entity.profiles, LinkedList(), teamBots, opponentBots, data).start().let { true }
        }

        check(!(teamSize > 1 && bots && botTeamSetting == BotTeamSetting.BOTH)) { "An unknown error occurred while trying to find a match" }

        val queueRecords = queueMap[queueAndGametypeHash] ?: return false

        val compatibleQueueRecords = RecordSet()

        for (record in queueRecords) if (queueRecord.isCompatible(record)) compatibleQueueRecords.add(record)

        if (compatibleQueueRecords.isEmpty()) return false

        compatibleQueueRecords.add(queueRecord)

        val requiredPlayers = teamSize * (if (bots) 1 else 2)
        val records = RecordSet()

        for (record in compatibleQueueRecords) if (records.playerCount() + record.entity.profiles.size <= requiredPlayers) records.add(
            record
        )

        if (records.playerCount() < requiredPlayers) return false

        for (record in records) queueRecords.remove(record)

        return startMatch(queueRecord, records, teamSize).let { true }
    }

    fun getQueueEntry(profile: Profile, queuetype: Queuetype, gametype: Gametype) =
        getQueueEntry(profile.party ?: profile, queuetype, gametype)

    private fun getQueueEntry(entity: QueuedEntity, queuetype: Queuetype, gametype: Gametype) =
        queueMap[queuetypeAndGametypeHash(queuetype, gametype)]?.find { it.entity == entity }?.queueEntry

    private fun queuetypeAndGametypeHash(queuetype: Queuetype, gametype: Gametype) =
        (queuetype.id.toInt() shl 8 or gametype.id.toInt()).toShort()

    fun getQueueEntries(profile: Profile) = getQueueEntries(profile.party ?: profile)

    private fun getQueueEntries(entity: QueuedEntity): List<QueueEntry> {
        val queueEntries = GlueList<QueueEntry>()

        for (recordSet in queueMap.values)
            for (record in recordSet)
                if (record.entity == entity)
                    queueEntries.add(record.queueEntry)

        return queueEntries
    }


    fun removePlayerFromQueue(profile: Profile, queuetype: Queuetype, gametype: Gametype) =
        removePlayerFromQueue(profile.party ?: profile, queuetype, gametype)

    private fun removePlayerFromQueue(entity: QueuedEntity?, queuetype: Queuetype, gametype: Gametype): Boolean {
        queueMap[queuetypeAndGametypeHash(queuetype, gametype)].removeIf { it.entity == entity }
        return queueMap.values.any { set -> set.any { it.entity == entity } }
    }

    fun removePlayerFromQueue(profile: Profile) = removePlayerFromQueue(profile.party ?: profile)

    private fun removePlayerFromQueue(player: QueuedEntity) {
        for (queueRecords in queueMap.values) queueRecords.removeIf { it.entity == player }
    }

    private fun startMatch(
        sampleRecord: QueueRecord, records: RecordSet, teamSize: Int
    ) {
        val allCompatibleArenas = ByteOpenHashSet(
            sampleRecord.queueEntry.queuetype
                .filterArenasByGametype(sampleRecord.queueEntry.gametype)
        )

        if (allCompatibleArenas.isEmpty()) return

        val commonArenaIds = ByteOpenHashSet(allCompatibleArenas)

        for ((_, queueEntry) in records) {
            val arenas = ByteOpenHashSet()

            val enabledArenas = queueEntry.enabledArenas
            val disabledArenas = ByteOpenHashSet()

            for (e in enabledArenas.byte2BooleanEntrySet()) {
                if (e.booleanValue) arenas.add(e.byteKey)
                else disabledArenas.add(e.byteKey)
            }

            if (arenas.isEmpty()) {
                arenas.addAll(allCompatibleArenas)
                arenas.removeAll(disabledArenas)
            } else arenas.removeAll(disabledArenas)

            if (arenas.isEmpty()) return

            commonArenaIds.retainAll(arenas)

            if (commonArenaIds.isEmpty()) return
        }

        val selectedArenaId: Byte
        if (commonArenaIds.size == 1) selectedArenaId = commonArenaIds.iterator().nextByte()
        else {
            val arenasArray = commonArenaIds.toByteArray()
            selectedArenaId = arenasArray[random.nextInt(arenasArray.size)]
        }

        val selectedArena = arenas[selectedArenaId]
        checkNotNull(selectedArena) { "Arena not found for ID: $selectedArenaId" }

        val teamA = LinkedList<Profile>()
        val teamB = LinkedList<Profile>()

        for (record in records) record.entity.profiles.let {
            if (teamA.size + it.size <= teamSize) teamA.addAll(it)
            else teamB.addAll(it)
        }

        val bots = sampleRecord.queueEntry.botsEnabled
        val matchData = MatchData(sampleRecord.queueEntry)
        matchData.arenaId = selectedArenaId

        val difficulty = Difficulty.entries[sampleRecord.queueEntry.opponentDifficulty.toInt()]

        if (bots) {
            teamA.addAll(teamB)
            val teamBBots = GlueList<BotConfiguration>()
            for (i in 0..<teamSize) teamBBots.add(difficulty.getConfiguration())
            BotTeamMatch(teamA, LinkedList(), GlueList(), teamBBots, matchData).start()
            return
        }

        if (teamSize == 1) Match(matchData, teamA[0], teamB[0]).start()
        else TeamMatch(teamA, teamB, matchData).start()
    }

    fun getCompatibleQueueCount(queuetype: Queuetype, gametype: Gametype): Int {
        val queueAndGametypeHash = (queuetype.id.toInt() shl 8 or gametype.id.toInt()).toShort()
        return queueMap.computeIfAbsent(queueAndGametypeHash, Short2ObjectFunction { k: Short -> RecordSet() }).size
    }

    private class RecordSet : ObjectOpenHashSet<QueueRecord>() {
        fun playerCount() = this.sumOf { it.entity.profiles.size }

        companion object {
            private const val serialVersionUID = 1L
        }
    }

    private data class QueueRecord(val entity: QueuedEntity, val queueEntry: QueueEntry) {
        override fun hashCode() = entity.hashCode()

        override fun equals(other: Any?): Boolean {
            if (other !is QueueRecord) return false
            if (javaClass != other.javaClass) return false
            return entity == other.entity
        }

        fun isCompatible(other: QueueRecord) = !equals(other) && queueEntry.isCompatible(other.queueEntry)
    }
}
