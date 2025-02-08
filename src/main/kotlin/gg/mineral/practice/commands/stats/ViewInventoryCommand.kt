package gg.mineral.practice.commands.stats

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import gg.mineral.practice.entity.Profile
import gg.mineral.practice.inventory.menus.InventoryStatsListMenu
import gg.mineral.practice.managers.ProfileManager.getInventoryStats
import gg.mineral.practice.util.messages.impl.ErrorMessages

@Command(name = "viewinventory")
class ViewInventoryCommand {
    @Execute
    fun execute(@Context profile: Profile, @Arg playerName: String) {
        val inventoryStats = getInventoryStats(playerName)

        if (inventoryStats.isNullOrEmpty()) return profile.message(ErrorMessages.PLAYER_INVENTORY_NOT_FOUND)

        val firstEntry = inventoryStats[0]

        profile.openMenu(
            if (inventoryStats.size == 1) firstEntry else InventoryStatsListMenu(
                inventoryStats,
                firstEntry.opponent
            )
        )
    }
}
