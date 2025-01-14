package gg.mineral.practice.managers

import gg.mineral.api.config.FileConfiguration
import gg.mineral.practice.util.config.BoolProp
import gg.mineral.practice.util.config.IntProp
import gg.mineral.practice.util.config.ItemStackProp
import gg.mineral.practice.util.config.StringProp
import gg.mineral.practice.util.items.ItemStacks

object LeaderboardManager {
    val config: FileConfiguration = FileConfiguration("leaderboardoptions.yml", "plugins/Practice")
    var slot by IntProp(config, "Leaderboard.Slot", 3)
    var displayItem by ItemStackProp(config, "Leaderboard.DisplayItem", ItemStacks.DEFAULT_LEADERBOARD_DISPLAY_ITEM)
    var displayName by StringProp(config, "Leaderboard.DisplayName", "Leaderboard")
    var enabled by BoolProp(config, "Leaderboard.Enable", true)
}
