package gg.mineral.practice.util.config


import gg.mineral.api.collection.GlueList
import gg.mineral.api.config.FileConfiguration
import gg.mineral.practice.kit.Kit
import gg.mineral.practice.util.items.ItemStacks
import org.bukkit.inventory.ItemStack

class KitProp(config: FileConfiguration, path: String, default: Kit, val name: String) :
    CachedProp<Kit>(config, path, default) {
    override fun readValue(): Kit {
        var cs = config.getConfigurationSection("$path.Armour")

        val armour = GlueList<ItemStack?>(4)

        if (cs != null) {
            for (key in cs.getKeys(false)) {
                val o = cs[key]
                if (o is ItemStack) armour.add(o)
                else armour.add(ItemStacks.AIR)
            }
        }

        cs = config.getConfigurationSection("$path.Contents")

        val items = GlueList<ItemStack?>(36)

        if (cs != null) {
            for (key in cs.getKeys(false)) {
                val o = cs[key]
                if (o is ItemStack) items.add(o)
                else items.add(ItemStacks.AIR)
            }
        }

        return Kit(this.name, items.toTypedArray<ItemStack?>(), armour.toTypedArray<ItemStack?>())
    }

    override fun writeValue(value: Kit) {
        val contents = value.contents
        val armourContents = value.armourContents

        for (i in contents.indices) {
            val item = contents[i]
            item?.let { config[path + "Contents." + i] = it } ?: run { config[path + "Contents." + i] = "empty" }
        }

        for (x in armourContents.indices) {
            val armour = armourContents[x]
            armour?.let { config[path + "Armour." + x] = it } ?: run { config[path + "Armour." + x] = "empty" }
        }
    }
}