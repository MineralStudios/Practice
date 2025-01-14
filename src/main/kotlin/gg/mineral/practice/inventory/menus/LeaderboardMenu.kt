package gg.mineral.practice.inventory.menus

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.category.Category
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.queue.QueuetypeMenuEntry
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap

@ClickCancelled(true)
open class LeaderboardMenu(protected val queuetype: Queuetype) : PracticeMenu() {
    protected open val menuEntries: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> by lazy { queuetype.menuEntries }

    override fun update() {
        val global = ItemStacks.GLOBAL_ELO.name(CC.SECONDARY + CC.B + "Global").build()
        val globalMeta = global.itemMeta

        try {
            globalMeta.lore = queuetype.globalLeaderboardLore
        } catch (e: Exception) {
            globalMeta.lore = null
        }

        global.setItemMeta(globalMeta)

        setSlot(4, global)

        for (entry in menuEntries.object2IntEntrySet()) {
            val menuEntry = entry.key

            if (shouldSkip(menuEntry)) continue

            val itemBuild = ItemBuilder(menuEntry.displayItem.clone())
                .name(CC.SECONDARY + CC.B + menuEntry.displayName)

            if (menuEntry is Gametype) {
                itemBuild.lore(*menuEntry.leaderboardLore.toTypedArray<String>())
                val item = itemBuild.build()
                setSlot(entry.intValue + 18, item)
                continue
            }

            if (menuEntry is Category) {
                val sb = GlueList<String>()
                sb.add(CC.SECONDARY + "Includes:")

                menuEntry.gametypes.map { GametypeManager.gametypes[it] }
                    .forEach { if (it.inCategory) sb.add(CC.WHITE + it.displayName) }

                sb.add(" ")
                sb.add(CC.BOARD_SEPARATOR)
                sb.add(CC.ACCENT + "Click to view category.")

                itemBuild.lore(*sb.toTypedArray<String>())
                val item = itemBuild.build()

                setSlot(
                    entry.intValue + 18, item
                ) { interaction: Interaction ->
                    interaction.profile
                        .openMenu(CategorizedLeaderboardMenu(queuetype, menuEntry))
                }
            }
        }
    }

    protected open fun shouldSkip(menuEntry: QueuetypeMenuEntry) = menuEntry is Gametype && menuEntry.inCategory

    override val title: String
        get() = CC.BLUE + "Leaderboards"

    override fun shouldUpdate() = true
}
