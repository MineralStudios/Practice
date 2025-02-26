package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.math.MathUtil
import gg.mineral.practice.util.messages.CC
import kotlin.math.max

@ClickCancelled(true)
class InventoryStatsListMenu(private val list: List<InventoryStatsMenu>, private val opponent: String? = null) :
    PracticeMenu() {
    override fun update() {
        for (inventoryStatsMenu in list) {
            inventoryStatsMenu.previousMenu = this
            add(
                ItemStacks.INVENTORY_STATS.name(CC.SECONDARY + CC.B + inventoryStatsMenu.title)
                    .lore(CC.ACCENT + "Click to view.").build()
            ) { interaction: Interaction ->
                interaction.profile.openMenu(
                    inventoryStatsMenu
                )
            }
        }

        val size = list.size
        val invSize = max(MathUtil.roundUp(size, 9).toDouble(), 9.0).toInt()
        val lastSlot = invSize - 1

        opponent?.let {
            setSlot(
                lastSlot, ItemStacks.VIEW_OPPONENT_INVENTORY
            ) { interaction: Interaction ->
                interaction.profile.player
                    ?.performCommand("viewinventory $it")
            }
        }
    }

    override val title: String
        get() {
            return if (list.isEmpty()) CC.RED + "No Inventories"
            else CC.BLUE + list[0].title + "'s Team"
        }

    override fun shouldUpdate() = false
}
