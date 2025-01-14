package gg.mineral.practice.match.data

import gg.mineral.api.knockback.Knockback
import gg.mineral.practice.duel.DuelSettings
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.kit.Kit
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.match.knockback.OldStyleKnockback
import gg.mineral.practice.queue.QueueSettings
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.items.ItemStacks
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import org.bukkit.inventory.ItemStack

class MatchData private constructor() {
    var queuetype: Queuetype? = null
        set(value) {
            this.knockback = value?.knockback
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
    val ranked = false
    var enabledArenas: Byte2BooleanOpenHashMap = Byte2BooleanOpenHashMap()
    private var displayItem: ItemStack = ItemStacks.WOOD_AXE

    constructor(queuetype: Queuetype, gametype: Gametype, queueSettings: QueueSettings) : this(queuetype, gametype) {
        this.oldCombat = queueSettings.oldCombat
        if (knockback == null && this.oldCombat) this.knockback = OldStyleKnockback()
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
        if (knockback == null && this.oldCombat) this.knockback = OldStyleKnockback()
    }

    constructor(duelSettings: DuelSettings) : this(duelSettings.queuetype, duelSettings.gametype) {
        this.arenaId = duelSettings.arenaId
        this.kit = if (duelSettings.kit == null)
            GametypeManager.gametypes.get(0.toByte()).kit
        else
            duelSettings.kit!!
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

    fun getCustomKits(p: Profile): Int2ObjectOpenHashMap<Array<ItemStack?>>? {
        if (queuetype == null || gametype == null) return Int2ObjectOpenHashMap()
        return p.getCustomKits(queuetype!!, gametype!!)
    }

    fun getElo(p: Profile) = gametype?.getElo(p) ?: 0

    val queueAndGameTypeHash: Short
        get() {
            val queuetypeId: Byte = queuetype?.id ?: 0
            val gametypeId: Byte = gametype?.id ?: 0
            return (queuetypeId.toInt() shl 8 or gametypeId.toInt()).toShort()
        }
}
