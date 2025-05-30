package gg.mineral.practice.inventory.menus

import gg.mineral.practice.category.Category
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.queue.QueuetypeMenuEntry
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap

@ClickCancelled(true)
class CategorizedEloMenu(
    arg: ProfileData,
    queuetype: Queuetype,
    private val category: Category,
    private val prevMenu: Menu
) :
    EloMenu(arg, queuetype) {
    override val menuEntries: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> by lazy {
        val value = Object2IntLinkedOpenHashMap<QueuetypeMenuEntry>()
        category.gametypes.mapNotNull { GametypeManager.gametypes[it] }
            .forEach { if (it.inCategory) value.put(it, queuetype.menuEntries.getInt(it)) }
        value
    }

    override fun shouldSkip(menuEntry: QueuetypeMenuEntry) = menuEntry is Gametype && !menuEntry.inCategory

    override fun update() {
        super.update()

        addOnNextRow(
            13, ItemStacks.BACK
        ) { viewer.openMenu(prevMenu) }
    }

    override val title: String
        get() = CC.BLUE + category.displayName

    override fun shouldUpdate() = true
}
