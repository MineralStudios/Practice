package gg.mineral.practice.duel

import gg.mineral.api.knockback.Knockback
import gg.mineral.practice.arena.Arena
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.kit.Kit
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.GametypeManager.gametypes
import gg.mineral.practice.match.knockback.OldStyleKnockback
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import gg.mineral.server.combat.KnockbackProfileList
import it.unimi.dsi.fastutil.bytes.Byte2BooleanOpenHashMap
import org.bukkit.inventory.ItemStack

class DuelSettings(queuetype: Queuetype? = null, gametype: Gametype? = null) {
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
    var enabledArenas: Byte2BooleanOpenHashMap = Byte2BooleanOpenHashMap()
    var kit: Kit? = null
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
            else if (!value) knockback = null
        }
    private var displayItem: ItemStack = ItemStacks.WOOD_AXE

    init {
        this.queuetype = queuetype
        this.gametype = gametype
    }

    fun enableArena(arena: Arena, enabled: Boolean) = enableArena(arena.id, enabled)

    fun enableArena(id: Byte, enabled: Boolean) = enabledArenas.put(id, enabled)

    override fun toString(): String {
        val sb = StringBuilder()
        val newLine = CC.R + "\n"

        val arenas = enabledArenas.filter { it.value }.keys.mapNotNull { arenas[it] }

        val knockback = if (this.knockback == null)
            if (noDamageTicks < 10)
                KnockbackProfileList.getComboKnockbackProfile()
            else
                KnockbackProfileList.getDefaultKnockbackProfile()
        else
            knockback!!

        val kit = this.kit ?: kit ?: gametypes[0.toByte()]?.kit ?: Kit.emptyKit

        sb.append(CC.GREEN).append("Kit: ").append(kit.name)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Arena Count: ").append(arenas.size)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Knockback: ").append(knockback.name)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Hit Delay: ").append(noDamageTicks)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Hunger: ").append(hunger)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Build: ").append(build)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Damage: ").append(damage)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Griefing: ").append(griefing)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Deadly Water: ").append(deadlyWater)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Regeneration: ").append(regeneration)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Old Combat: ").append(oldCombat)
        // TODO 2v2 with bots in /duel
        // sb.append(newLine);
        // sb.append(CC.GREEN + "2v2: " + team2v2);
        // sb.append(newLine);
        // sb.append(CC.GREEN + "Bots: " + bots);
        sb.append(newLine)
        sb.append(CC.GREEN).append("Boxing: ").append(boxing)
        sb.append(newLine)
        sb.append(CC.GREEN).append("Pearl Cooldown: ").append(pearlCooldown).append(" seconds")
        return sb.toString()
    }
}
