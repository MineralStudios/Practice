package gg.mineral.practice.inventory.menus

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.GametypeManager.gametypes
import gg.mineral.practice.managers.QueuetypeManager.queuetypes
import gg.mineral.practice.queue.QueueSystem
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.messages.CC

@ClickCancelled(true)
class QueueManagerMenu : PracticeMenu() {
    override fun update() {
        clear()
        val queueEntries = QueueSystem.getQueueEntries(viewer)

        for (queueEntry in queueEntries) {
            val g = gametypes[queueEntry.gametype.id]
            val q = queuetypes[queueEntry.queuetype.id]

            if (g == null || q == null) continue

            val categoryName = if (g.categoryName.isEmpty()) g.categoryName + " " else ""

            val item = ItemBuilder(g.displayItem)
                .name(CC.SECONDARY + CC.B + q.displayName + " " + categoryName + g.displayName)
                .lore(CC.RED + "Click to leave queue.").build()

            add(item) { interaction: Interaction ->
                val p = interaction.profile
                p.removeFromQueue(q, g)
                if (p.playerStatus === PlayerStatus.QUEUEING) reload()
            }
        }
    }

    override val title: String
        get() = CC.BLUE + "Queue Manager"

    override fun shouldUpdate() = true
}
