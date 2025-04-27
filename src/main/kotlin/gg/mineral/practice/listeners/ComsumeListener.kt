package gg.mineral.practice.listeners

import gg.mineral.practice.util.items.ItemStacks
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ComsumeListener : Listener {
    @EventHandler
    fun onConsume(e: PlayerItemConsumeEvent) {
        when (e.item.type) {
            Material.POTION -> e.replacement = ItemStacks.AIR
            Material.GOLDEN_APPLE -> {
                val goldenHead = e.item
                try {
                    if (goldenHead.itemMeta.displayName.equals("Golden Head", ignoreCase = true)) {
                        val p = e.player
                        p.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 1))
                        p.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 1600, 0))
                    }
                } catch (_: Exception) {
                }
            }

            else -> {}
        }
    }
}
