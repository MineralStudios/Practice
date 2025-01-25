package gg.mineral.practice.inventory.menus

import gg.mineral.practice.category.Category
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.messages.CC
import org.bukkit.inventory.ItemStack

@ClickCancelled(true)
class SelectCategorizedExistingKitMenu(private val category: Category, menu: PracticeMenu, simple: Boolean) :
    SelectExistingKitMenu(menu, simple, menu) {
    override val title: String
        get() = CC.BLUE + category.name

    override fun update() {
        category.gametypes.mapNotNull { GametypeManager.gametypes[it] }.forEach { g ->
            val item: ItemStack = ItemBuilder(g.displayItem.clone())
                .name(CC.SECONDARY + CC.B + g.displayName).lore(CC.ACCENT + "Click to select.").build()
            add(item) {
                if (viewer.playerStatus === PlayerStatus.KIT_CREATOR) {
                    viewer.giveKit(g.kit)
                    return@add
                }
                if (simple) viewer.duelSettings.gametype = g
                else viewer.duelSettings.kit = g.kit
                viewer.openMenu(menu)
            }
        }
    }
}
