package gg.mineral.practice.category

import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.managers.CategoryManager
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.queue.QueuetypeMenuEntry
import gg.mineral.practice.util.config.ItemStackProp
import gg.mineral.practice.util.config.StringProp
import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.items.ItemStacks
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet

class Category(val name: String, val id: Byte) : QueuetypeMenuEntry {
    val config: FileConfiguration = CategoryManager.config
    val path: String = "Category.$name."
    override var displayItem by ItemStackProp(config, path + "DisplayItem", ItemStacks.DEFAULT_CATEGORY_DISPLAY_ITEM)
    override var displayName by StringProp(config, path + "DisplayName", name)
    override val botsEnabled: Boolean
        get() {
            val iterator = gametypes.iterator()
            while (iterator.hasNext()) {
                val gametypeId = iterator.nextByte()
                if (GametypeManager.gametypes[gametypeId].botsEnabled) return true
            }
            return false
        }

    val gametypes by lazy {
        val set = ByteOpenHashSet()
        for (gametype in GametypeManager.gametypes.values)
            if (gametype.inCategory && gametype.categoryName.equals(name, ignoreCase = true)) set.add(gametype.id)
        set
    }

    fun addGametype(gametype: Gametype) {
        gametypes.add(gametype.id)

        // TODO: change how this operates
        gametype.inCategory = true
        gametype.categoryName = name
    }

    fun removeGametype(gametype: Gametype) {
        gametypes.remove(gametype.id)
        // TODO: change how this operates
        gametype.inCategory = false
    }

    override fun equals(other: Any?): Boolean {
        if (other is Category) return other.name.equals(this.name, ignoreCase = true)
        return false
    }

    override fun hashCode() = name.hashCode()

    fun delete() {
        config.remove("Category.$name")
        config.save()
    }
}
