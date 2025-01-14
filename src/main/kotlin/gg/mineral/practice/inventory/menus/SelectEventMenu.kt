package gg.mineral.practice.inventory.menus

import gg.mineral.practice.events.Event
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.GametypeManager.gametypes
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ErrorMessages

@ClickCancelled(true)
class SelectEventMenu : PracticeMenu() {
    override fun update() {
        for (g in gametypes.values) {
            if (!g.event) continue

            val item = ItemBuilder(g.displayItem)
                .name(
                    (CC.SECONDARY + CC.B
                            + (if (g.inCategory) g.categoryName + g.displayName else g.displayName))
                )
                .lore(CC.ACCENT + "Click to start event.")
                .build()

            add(item) {
                viewer.player.closeInventory()
                viewer.duelSettings.gametype = g

                if (g.eventArenaId.toInt() == -1) {
                    viewer.message(ErrorMessages.ARENA_NOT_FOUND)
                    return@add
                }

                val event = Event(viewer, g.eventArenaId)
                event.start()
            }
        }
    }

    override val title: String
        get() = CC.BLUE + "Select Event"

    override fun shouldUpdate() = true
}
