package gg.mineral.practice.managers

import gg.mineral.practice.util.config.BoolProp
import gg.mineral.practice.util.config.IntProp
import gg.mineral.practice.util.config.ItemStackProp
import gg.mineral.practice.util.config.StringProp
import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.items.ItemStacks

object PlayerSettingsManager {
    val config: FileConfiguration = FileConfiguration("playeroptions.yml", "plugins/Practice")
    var slot by IntProp(config, "Options.Slot", 3)
    var displayItem by ItemStackProp(config, "Options.DisplayItem", ItemStacks.DEFAULT_OPTIONS_DISPLAY_ITEM)
    var displayName by StringProp(config, "Options.DisplayName", "Settings")
    var enabled by BoolProp(config, "Options.Enable", true)
}