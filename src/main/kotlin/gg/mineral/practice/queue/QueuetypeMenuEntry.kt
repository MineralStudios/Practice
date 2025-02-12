package gg.mineral.practice.queue

import org.bukkit.inventory.ItemStack

interface QueuetypeMenuEntry {
    val botsEnabled: Boolean

    val displayItem: ItemStack

    val displayName: String
}
