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
open class EloMenu(private val profileData: ProfileData, protected val queuetype: Queuetype) : AsyncMenu() {
    protected open val menuEntries: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> by lazy { queuetype.menuEntries }
    override val title: String
        get() = CC.BLUE + profileData.name

    protected open fun shouldSkip(menuEntry: QueuetypeMenuEntry) = menuEntry is Gametype && menuEntry.inCategory

    override fun update() {
        setSlot(
            4,
            ItemStacks.GLOBAL_ELO.name(
                CC.SECONDARY + CC.B + profileData.name + "'s Global Elo"
            )
                .lore(
                    (CC.WHITE + "The " + CC.SECONDARY + "average elo" + CC.WHITE
                            + " across all game types."),
                    " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + queuetype.getGlobalElo(profileData).join()
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
                        CC.WHITE + profileData.name + "'s Elo:",
                        CC.GOLD + menuEntry.getElo(profileData).join()
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
                        .openMenu(CategorizedEloMenu(profileData, queuetype, menuEntry, this))
                }
            }
        }
    }

    override fun shouldUpdate() = true
}
