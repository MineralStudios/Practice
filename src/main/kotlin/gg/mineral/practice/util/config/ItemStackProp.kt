package gg.mineral.practice.util.config

import gg.mineral.api.config.FileConfiguration
import org.bukkit.inventory.ItemStack

class ItemStackProp(config: FileConfiguration, path: String, default: ItemStack) :
    CachedProp<ItemStack>(config, path, default) {
    override fun readValue(): ItemStack = config.getItemstack(path, default)

    override fun writeValue(value: ItemStack) {
        config[path] = value
    }
}