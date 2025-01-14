package gg.mineral.practice.managers

import gg.mineral.api.config.FileConfiguration
import gg.mineral.practice.util.config.*
import gg.mineral.practice.util.items.ItemStacks
import org.bukkit.Bukkit
import org.bukkit.Location

object KitEditorManager {
    val config: FileConfiguration = FileConfiguration("kiteditor.yml", "plugins/Practice")
    var displayName by StringProp(config, "KitEditor.DisplayName", "Kit Editor")
    var displayItem by ItemStackProp(config, "KitEditor.DisplayItem", ItemStacks.DEFAULT_KIT_EDITOR_DISPLAY_ITEM)
    var slot by IntProp(config, "KitEditor.Slot", 0)
    var enabled by BoolProp(config, "KitEditor.Enable", true)
    var location by LocationProp(config, "KitEditor.Location", Location(Bukkit.getWorlds()[0], 0.0, 70.0, 0.0))
}
