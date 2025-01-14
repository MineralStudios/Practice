package gg.mineral.practice.queue

import gg.mineral.practice.arena.Arena
import gg.mineral.practice.bots.Difficulty
import gg.mineral.practice.gametype.Gametype
import it.unimi.dsi.fastutil.bytes.Byte2BooleanMap
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
import it.unimi.dsi.fastutil.bytes.ByteSet
import java.util.*


class QueueSettings {
    var teamSize: Byte = 1
    var opponentDifficulty: Byte = 0
    var teammateDifficulty: Byte = 0
    var botQueue = false
    var arenaSelection = true
    var oldCombat = false
    val enabledArenas: Byte2BooleanOpenHashMap = Byte2BooleanOpenHashMap()
    var botTeamSetting = BotTeamSetting.BOTH
    var customBotConfiguration = Difficulty.EASY.getConfiguration()

    enum class BotTeamSetting {
        BOTH, OPPONENT,
    }

    data class QueueEntry(
        val queuetype: Queuetype,
        val gametype: Gametype,
        val teamSize: Int,
        val botsEnabled: Boolean,
        val oldCombat: Boolean,
        val opponentDifficulty: Byte,
        val botTeamSetting: BotTeamSetting,
        val enabledArenas: Byte2BooleanOpenHashMap
    ) {
        fun isCompatible(entry: QueueEntry): Boolean {
            return queuetype === entry.queuetype && gametype === entry.gametype && teamSize == entry.teamSize && botsEnabled == entry.botsEnabled && oldCombat == entry.oldCombat && (teamSize <= 1 || !botsEnabled || !entry.botsEnabled || botTeamSetting == entry.botTeamSetting)
                    && (!botsEnabled || opponentDifficulty == entry.opponentDifficulty)
                    && (enabledArenas.isEmpty() || entry.enabledArenas.isEmpty() || enabledArenas.byte2BooleanEntrySet()
                .stream()
                .anyMatch { arena: Byte2BooleanMap.Entry -> entry.enabledArenas[arena.byteKey] == arena.booleanValue })
        }
    }

    val opponentBotDifficulty: Difficulty
        get() = Difficulty.entries[opponentDifficulty.toInt()]

    val teammateBotDifficulty: Difficulty
        get() = Difficulty.entries[teammateDifficulty.toInt()]

    fun enableArena(arena: Arena, enabled: Boolean) = enableArena(arena.id, enabled)

    fun enableArena(id: Byte, enabled: Boolean) = enabledArenas.put(id, enabled)

    companion object {
        fun toEntry(
            queuetype: Queuetype, gametype: Gametype, teamSize: Int, botsEnabled: Boolean,
            oldCombat: Boolean,
            opponentDifficulty: Byte,
            bot2v2Setting: BotTeamSetting,
            enabledArenas: Byte2BooleanOpenHashMap
        ): QueueEntry {
            return QueueEntry(
                queuetype, gametype, teamSize,
                queuetype.botsEnabled && gametype.botsEnabled && botsEnabled, oldCombat,
                opponentDifficulty, bot2v2Setting,
                enabledArenas
            )
        }

        fun getEnabledArenaIds(uuid: UUID): ByteSet {
            val leastSigBits = uuid.leastSignificantBits
            val enabledArenaIds = ByteOpenHashSet()

            for (index in 0..63) if ((leastSigBits and (1L shl index)) != 0L) enabledArenaIds.add(index.toByte())

            return enabledArenaIds
        }
    }
}
