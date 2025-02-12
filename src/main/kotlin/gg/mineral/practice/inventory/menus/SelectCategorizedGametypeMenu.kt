package gg.mineral.practice.inventory.menus

import gg.mineral.practice.category.Category
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.queue.QueuetypeMenuEntry
import gg.mineral.practice.util.messages.CC
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap

@ClickCancelled(true)
class SelectCategorizedGametypeMenu(queuetype: Queuetype, val category: Category, type: Type, prevMenu: Menu? = null) :
    SelectGametypeMenu(queuetype, type, prevMenu) {

    override fun setMenuEntries(): Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> {
        val menuEntries = Object2IntLinkedOpenHashMap<QueuetypeMenuEntry>()
        category.gametypes.mapNotNull { GametypeManager.gametypes[it] }.forEach { gametype ->
            if (gametype.inCategory) menuEntries.put(
                gametype,
                queuetype.menuEntries.getInt(gametype)
            )
        }
        return menuEntries
    }

    override val title: String
        get() = CC.BLUE + category.name

    override fun shouldSkip(menuEntry: QueuetypeMenuEntry) = menuEntry is Gametype && !menuEntry.inCategory

    override fun onClose() {
        if (viewer.playerStatus === PlayerStatus.FIGHTING || viewer.playerStatus === PlayerStatus.KIT_CREATOR || viewer.playerStatus === PlayerStatus.KIT_EDITOR || viewer.openMenu != null) return
        viewer.openMenu(SelectGametypeMenu(queuetype, type, null))
    }
}
