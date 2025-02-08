package gg.mineral.practice.match.data

import gg.mineral.api.knockback.Knockback
import gg.mineral.practice.duel.DuelSettings
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.kit.Kit
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.match.knockback.OldStyleKnockback
import gg.mineral.practice.queue.QueueSettings
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.items.ItemStacks
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet
import it.unimi.dsi.fastutil.bytes.ByteSet
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.bukkit.inventory.ItemStack
import java.util.concurrent.CompletableFuture

class MatchData private constructor() {
    var queuetype: Queuetype? = null
        set(value) {
            this.knockback = value?.knockback
            this.ranked = value?.ranked ?: false
            field = value
        }
    var gametype: Gametype? = null
        set(value) {
            value?.let {
                this.displayItem = it.displayItem.clone()
                this.kit = it.kit
                this.noDamageTicks = it.noDamageTicks
                this.hunger = it.hunger
                this.boxing = it.boxing
                this.build = it.build
                this.damage = it.damage
                this.griefing = it.griefing
                this.deadlyWater = it.deadlyWater
                this.regeneration = it.regeneration
                this.pearlCooldown = it.pearlCooldown
            }
            field = value
        }

    var arenaId: Byte = 0
    lateinit var kit: Kit
    var knockback: Knockback? = null
    var noDamageTicks = 20
    var pearlCooldown = 15
    var hunger = true
    var boxing = false
    var build = false
    var damage = true
    var griefing = false
    var deadlyWater = false
    var regeneration = true
    var oldCombat = false
        set(value) {
            field = value
            if (knockback == null && value) knockback = OldStyleKnockback()
        }
    var ranked = false
    var enabledArenas: Byte2BooleanOpenHashMap = Byte2BooleanOpenHashMap()
    private var displayItem: ItemStack = ItemStacks.WOOD_AXE

    constructor(queuetype: Queuetype, gametype: Gametype, queueSettings: QueueSettings) : this(queuetype, gametype) {
        this.oldCombat = queueSettings.oldCombat
        this.enabledArenas = Byte2BooleanOpenHashMap(queueSettings.enabledArenas)
    }

    constructor(queueEntry: QueueSettings.QueueEntry, queueSettings: QueueSettings) : this(
        queueEntry.queuetype,
        queueEntry.gametype,
        queueSettings
    )

    constructor(queuetype: Queuetype?, gametype: Gametype?) : this() {
        this.queuetype = queuetype
        this.gametype = gametype
    }

    constructor(queueEntry: QueueSettings.QueueEntry) : this(queueEntry.queuetype, queueEntry.gametype) {
        this.oldCombat = queueEntry.oldCombat
    }

    constructor(duelSettings: DuelSettings) : this(duelSettings, duelSettings.enabledArenas)

    constructor(duelSettings: DuelSettings, enabledArenas: Byte2BooleanOpenHashMap) : this(
        duelSettings.queuetype,
        duelSettings.gametype
    ) {
        this.enabledArenas = enabledArenas
        this.arenaId = nextArenaIdFiltered(queueCompatibleArenas() ?: arenas.keys)
        this.kit = duelSettings.kit ?: GametypeManager.gametypes.get(0.toByte())?.kit ?: Kit.emptyKit
        this.knockback = duelSettings.knockback
        this.noDamageTicks = duelSettings.noDamageTicks
        this.hunger = duelSettings.hunger
        this.boxing = duelSettings.boxing
        this.build = duelSettings.build
        this.damage = duelSettings.damage
        this.griefing = duelSettings.griefing
        this.deadlyWater = duelSettings.deadlyWater
        this.regeneration = duelSettings.regeneration
        this.pearlCooldown = duelSettings.pearlCooldown
        this.oldCombat = duelSettings.oldCombat
    }

    fun getCustomKits(p: Profile): Int2ObjectOpenHashMap<Array<ItemStack?>> {
        if (queuetype == null || gametype == null) return Int2ObjectOpenHashMap()
        return p.getCustomKits(queuetype!!, gametype!!)
    }

    fun getElo(p: Profile): CompletableFuture<Int> = gametype?.getElo(p) ?: CompletableFuture.completedFuture(1000)

    private fun nextArenaId(compatibleArenas: ByteSet?) = compatibleArenas?.random() ?: -1

    private fun queueCompatibleArenas(): ByteOpenHashSet? =
        gametype?.let { queuetype?.filterArenasByGametype(it) }

    fun nextArenaIdFiltered(compatibleArenas: ByteSet?): Byte {
        val filteredArenas =
            compatibleArenas?.filter { enabledArenas[it] }
        if (filteredArenas?.isEmpty() == true) return nextArenaId(compatibleArenas)
        return filteredArenas?.random() ?: nextArenaId(compatibleArenas)
    }

    val queueAndGameTypeHash: Short
        get() {
            val queuetypeId: Byte = queuetype?.id ?: 0
            val gametypeId: Byte = gametype?.id ?: 0
            return (queuetypeId.toInt() shl 8 or gametypeId.toInt()).toShort()
        }

    fun deriveDuelSettings(): DuelSettings {
        val duelSettings = DuelSettings()
        duelSettings.queuetype = queuetype
        duelSettings.gametype = gametype
        duelSettings.enabledArenas = Byte2BooleanOpenHashMap().apply { put(arenaId, true) }
        duelSettings.kit = kit
        duelSettings.knockback = knockback
        duelSettings.noDamageTicks = noDamageTicks
        duelSettings.pearlCooldown = pearlCooldown
        duelSettings.hunger = hunger
        duelSettings.boxing = boxing
        duelSettings.build = build
        duelSettings.damage = damage
        duelSettings.griefing = griefing
        duelSettings.deadlyWater = deadlyWater
        duelSettings.regeneration = regeneration
        duelSettings.oldCombat = oldCombat
        return duelSettings
    }
}
