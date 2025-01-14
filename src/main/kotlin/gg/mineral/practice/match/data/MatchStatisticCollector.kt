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
    private var clickCounterStart: Long = 0
    lateinit var inventoryContents: Array<ItemStack>
    var helmet: ItemStack? = null
    var chestplate: ItemStack? = null
    var leggings: ItemStack? = null
    var boots: ItemStack? = null
    private var potionEffectStrings: MutableList<String>? = null
    private var active: Boolean = false
    var alive: Boolean = false

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
        hitCount = 0
        currentCombo = 0
        clickCounterStart = System.currentTimeMillis()
        potionEffectStrings = GlueList()
    }

    fun end(alive: Boolean) {
        if (!active) return

        active = false
        this.alive = alive
        this.potionsRemaining = profile.inventory.getNumber(Material.POTION, 16421.toShort())
        this.soupsRemaining = profile.inventory.getNumber(Material.MUSHROOM_SOUP)
        this.remainingHealth = if (profile.player.isDead) 0 else profile.player.health.toInt()
        this.potionAccuracy = (100 - (potionsMissed * 100.0
                / potionsThrown)).toInt()
        this.wTapAccuracy = (wTapCount * 100.0
                / hitCount).toInt()
        this.inventoryContents = profile.inventory.contents
        this.helmet = profile.inventory.helmet
        this.chestplate = profile.inventory.chestplate
        this.leggings = profile.inventory.leggings
        this.boots = profile.inventory.boots

        for (potionEffect in profile.player.activePotionEffects) {
            val romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.amplifier + 1)
            val effectName = StringUtil.toNiceString(potionEffect.type.name.lowercase(Locale.getDefault()))
            val duration = MathUtil.convertTicksToMinutes(potionEffect.duration)
            potionEffectStrings!!.add(
                (ChatColor.YELLOW.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + effectName
                        + " " + romanNumeral + ChatColor.GRAY + " (" + duration + ")")
            )
        }
    }

    fun increaseHitCount() {
        if (!active) return

        hitCount++
        currentCombo++

        if (profile.player.handle.isSprinting) wTapCount++

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

        if (System.currentTimeMillis() - clickCounterStart > 1000) {
            clickCounterStart = System.currentTimeMillis()
            highestCps = max(clicks.toDouble(), highestCps.toDouble()).toInt()
            clicks = 1
        }

        clicks++
    }

    val potionEffectStringArray: Array<String>
        get() = potionEffectStrings!!.toTypedArray<String>()
}
