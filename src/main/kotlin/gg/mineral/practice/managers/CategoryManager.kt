package gg.mineral.practice.managers

import gg.mineral.practice.category.Category
import gg.mineral.practice.managers.QueuetypeManager.queuetypes
import gg.mineral.practice.util.config.yaml.FileConfiguration
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import org.bukkit.configuration.ConfigurationSection

object CategoryManager {
    val config: FileConfiguration = FileConfiguration("category.yml", "plugins/Practice")
    val categories: Byte2ObjectOpenHashMap<Category> = Byte2ObjectOpenHashMap()
    var CURRENT_ID: Byte = 0

    fun registerCategory(category: Category): Category? = categories.put(category.id, category)

    fun remove(category: Category) {
        categories.remove(category.id)
        category.delete()

        for (queuetype in queuetypes.values) queuetype.menuEntries.removeInt(category)
    }

    fun contains(category: Category): Boolean {
        for (c in categories) if (c.equals(category)) return true
        return false
    }

    fun getCategoryByName(string: String): Category? {
        for (category in categories.values) if (category.name.equals(string, ignoreCase = true)) return category
        return null
    }

    fun load() {
        val configSection: ConfigurationSection = config.getConfigurationSection("Category.") ?: return

        for (key in configSection.getKeys(false)) {
            if (key == null) continue
            registerCategory(Category(key, CURRENT_ID++))
        }
    }
}
