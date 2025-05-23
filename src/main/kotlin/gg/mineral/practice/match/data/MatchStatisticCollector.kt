package gg.mineral.practice.match.data

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.math.MathUtil
import gg.mineral.practice.util.messages.StringUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MatchStatisticCollector(val profile: Profile) {
    var hitCount: Int = 0
    private var currentCombo: Int = 0
    var longestCombo: Int = 0
    var averageCombo: Int = 0
    var highestCps: Int = 0
    private var wTapCount: Int = 0
    var wTapAccuracy: Int = 0
    var potionsThrown: Int = 0
    var potionsMissed: Int = 0
    var potionsStolen: Int = 0
    var potionsRemaining: Int = 0
    var potionAccuracy: Int = 0
    var soupsRemaining: Int = 0
    var remainingHealth: Int = 0
    private var clicks: Int = 0
    var totalClicks = 0
    private var clickCounterStart: Long = 0
    lateinit var inventoryContents: Array<ItemStack?>
    var helmet: ItemStack? = null
    var chestplate: ItemStack? = null
    var leggings: ItemStack? = null
    var boots: ItemStack? = null
    private var potionEffectStrings: MutableList<String>? = null
    private var active: Boolean = false
    var alive: Boolean = false
    val potionEffectStringArray: Array<String>
        get() = potionEffectStrings!!.toTypedArray<String>()
    private val cpsRecord = mutableListOf<Int>()
    val averageCps: Int
        get() = if (cpsRecord.isEmpty()) 0 else cpsRecord.average().roundToInt()
    val cpsDeviation: Int
        get() {
            if (cpsRecord.isEmpty()) return 0
            val mean = averageCps.toDouble()
            val variance = cpsRecord.map { (it - mean) * (it - mean) }.average()
            return sqrt(variance).roundToInt()
        }

    fun start() {
        check(!active) { "Already started" }

        active = true
        clicks = 0
        soupsRemaining = 0
        potionAccuracy = 0
        potionsRemaining = 0
        potionsStolen = 0
        potionsMissed = 0
        potionsThrown = 0
        wTapAccuracy = 0
        wTapCount = 0
        highestCps = 0
        averageCombo = 0
        longestCombo = 0
        remainingHealth = 0
        hitCount = 0
        currentCombo = 0
        clickCounterStart = profile.clientTimeMillis
        potionEffectStrings = GlueList()
    }

    fun end(alive: Boolean) {
        if (!active) return

        active = false
        this.alive = alive
        this.potionsRemaining = profile.inventory.getNumber(Material.POTION, 16421.toShort())
        this.soupsRemaining = profile.inventory.getNumber(Material.MUSHROOM_SOUP)
        this.remainingHealth = profile.player?.let { if (it.isDead) 0 else it.health.toInt() } ?: 0
        this.potionAccuracy = (100 - (potionsMissed * 100.0
                / potionsThrown)).toInt()
        this.wTapAccuracy = (wTapCount * 100.0
                / hitCount).toInt()
        this.inventoryContents = profile.inventory.contents
        this.helmet = profile.inventory.helmet
        this.chestplate = profile.inventory.chestplate
        this.leggings = profile.inventory.leggings
        this.boots = profile.inventory.boots

        profile.player?.apply {
            for (potionEffect in activePotionEffects) {
                val romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.amplifier + 1)
                val effectName = StringUtil.toNiceString(potionEffect.type.name.lowercase(Locale.getDefault()))
                val duration = MathUtil.convertTicksToMinutes(potionEffect.duration)
                potionEffectStrings!!.add(
                    (ChatColor.YELLOW.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + effectName
                            + " " + romanNumeral + ChatColor.GRAY + " (" + duration + ")")
                )
            }
        }
    }

    fun increaseHitCount() {
        if (!active) return

        hitCount++
        currentCombo++

        if (profile.player?.handle?.isSprinting == true) wTapCount++

        if (currentCombo > 1) {
            averageCombo += currentCombo
            averageCombo /= 2
        }

        longestCombo = max(currentCombo.toDouble(), longestCombo.toDouble()).toInt()
    }

    fun resetCombo() {
        if (!active) return

        currentCombo = 0
    }

    fun clearHitCount() {
        if (!active) return

        hitCount = 0
    }

    fun thrownPotion(missed: Boolean) {
        if (!active) return

        potionsThrown++

        if (missed) potionsMissed++
    }

    fun stolenPotion() {
        if (!active) return

        potionsStolen++
    }

    fun click() {
        if (!active) return

        if (profile.clientTimeMillis - clickCounterStart >= 1000) {
            clickCounterStart = profile.clientTimeMillis
            highestCps = max(clicks.toDouble(), highestCps.toDouble()).toInt()
            if (clicks > min(1, highestCps)) cpsRecord.add(clicks)
            clicks = 0
        }

        clicks++
        totalClicks++
    }
}
