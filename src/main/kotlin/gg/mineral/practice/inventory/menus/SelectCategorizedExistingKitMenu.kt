package gg.mineral.practice.inventory.menus

import gg.mineral.practice.category.Category
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.util.messages.CC

@ClickCancelled(true)
class SelectCategorizedExistingKitMenu(
    private val category: Category,
    onSelect: (Gametype) -> Unit,
    simple: Boolean,
    prevMenu: Menu? = null
) :
    SelectExistingKitMenu(onSelect, simple, prevMenu) {
    override val title: String
        get() = CC.BLUE + category.name

    override fun update() {
        clear()
        addGametypes(category.gametypes.mapNotNull { GametypeManager.gametypes[it] })
    }
}
