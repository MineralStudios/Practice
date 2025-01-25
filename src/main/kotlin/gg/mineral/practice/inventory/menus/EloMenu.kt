package gg.mineral.practice.inventory.menus

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.category.Category
import gg.mineral.practice.entity.ProfileData
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.AsyncMenu
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.queue.QueuetypeMenuEntry
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap

@ClickCancelled(true)
open class EloMenu(protected var arg: ProfileData, protected var queuetype: Queuetype) : AsyncMenu() {
    protected open val menuEntries: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> by lazy { queuetype.menuEntries }

    protected open fun shouldSkip(menuEntry: QueuetypeMenuEntry) = menuEntry is Gametype && menuEntry.inCategory

    override fun update() {
        setSlot(
            4,
            ItemStacks.GLOBAL_ELO.name(
                CC.SECONDARY + CC.B + arg.name + "'s Global Elo"
            )
                .lore(
                    (CC.WHITE + "The " + CC.SECONDARY + "average elo" + CC.WHITE
                            + " across all game types."),
                    " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + queuetype.getGlobalElo(arg).join()
                )

                .build()
        )

        for (entry in menuEntries.object2IntEntrySet()) {
            val menuEntry = entry.key
            if (shouldSkip(menuEntry)) continue

            if (menuEntry is Gametype) {
                val item = ItemBuilder(menuEntry.displayItem)
                    .name(CC.SECONDARY + CC.B + menuEntry.displayName)
                    .lore(
                        " ",
                        CC.WHITE + arg.name + "'s Elo:",
                        CC.GOLD + menuEntry.getElo(arg).join()
                    )
                    .build()
                setSlot(entry.intValue + 18, item)
                continue
            }

            if (menuEntry is Category) {
                val itemBuild = ItemBuilder(menuEntry.displayItem)
                    .name(CC.SECONDARY + CC.B + menuEntry.displayName)

                val sb = GlueList<String>()
                sb.add(CC.SECONDARY + "Includes:")

                menuEntry.gametypes.mapNotNull { GametypeManager.gametypes[it] }
                    .forEach { sb.add(CC.WHITE + it.displayName) }

                sb.add(" ")
                sb.add(CC.BOARD_SEPARATOR)
                sb.add(CC.ACCENT + "Click to view category.")

                itemBuild.lore(*sb.toTypedArray<String>())
                val item = itemBuild.build()

                setSlot(
                    entry.intValue + 18, item
                ) {
                    it.profile
                        .openMenu(CategorizedEloMenu(arg, queuetype, menuEntry, this))
                }
            }
        }
    }

    override val title: String
        get() = CC.BLUE + arg.name

    override fun shouldUpdate() = true
}
