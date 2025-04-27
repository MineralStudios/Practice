package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.ChatColor
import org.bukkit.inventory.ItemStack

@ClickCancelled(true)
class QueuetypeArenaEnableMenu(val queuetype: Queuetype) : PracticeMenu() {

    override fun update() {
        clear()

        val arenas = arenas.values

        for (a in arenas) {
            a ?: continue
            val arenaEnabled = queuetype.arenas.contains(a.id)
            val color = if (arenaEnabled) ChatColor.GREEN else ChatColor.RED

            val item: ItemStack
            try {
                item = ItemBuilder(a.displayItem)
                    .name(CC.SECONDARY + CC.B + a.displayName).lore(color.toString() + arenaEnabled).build()
            } catch (e: Exception) {
                continue
            }

            add(item) {
                queuetype.enableArena(a, !arenaEnabled)
                viewer.message(
                    ChatMessages.QUEUETYPE_ARENA_SET.clone().replace("%queuetype%", queuetype.name)
                        .replace("%toggled%", "" + !arenaEnabled).replace("%arena%", a.name)
                )
                reload()
            }
        }
    }

    override val title: String
        get() = CC.BLUE + "Toggle Arena"

    override fun shouldUpdate() = true
}
