package gg.mineral.practice.managers

import gg.mineral.api.config.FileConfiguration
import gg.mineral.practice.util.config.BoolProp
import gg.mineral.practice.util.config.IntProp
import gg.mineral.practice.util.config.ItemStackProp
import gg.mineral.practice.util.config.StringProp
import gg.mineral.practice.util.items.ItemStacks

object SpectateManager {
    val config: FileConfiguration = FileConfiguration("spectateoptions.yml", "plugins/Practice")
    var slot by IntProp(config, "Spectate.Slot", 3)
    var displayItem by ItemStackProp(config, "Spectate.DisplayItem", ItemStacks.DEFAULT_SPECTATE_DISPLAY_ITEM)
    var displayName by StringProp(config, "Spectate.DisplayName", "Spectate")
    var enabled by BoolProp(config, "Spectate.Enable", true)
}
