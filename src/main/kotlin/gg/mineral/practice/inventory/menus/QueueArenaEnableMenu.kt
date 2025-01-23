package gg.mineral.practice.inventory.menus

import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import java.util.function.Consumer

@ClickCancelled(true)
class QueueArenaEnableMenu(
    private val queuetype: Queuetype,
    private val gametype: Gametype,
    private val queueInteraction: Consumer<Interaction>,
    private val prevMenu: Menu
) : PracticeMenu() {

    override fun update() {
        clear()
        val arenas = queuetype.filterArenasByGametype(gametype).iterator()
        val queueSettings = viewer.queueSettings

        while (arenas.hasNext()) {
            val arenaId = arenas.nextByte()
            val arenaEnabled = queueSettings.enabledArenas[arenaId]

            val arena = ArenaManager.arenas[arenaId] ?: continue

            val displayItem = arena.displayItem

            val displayName = arena.displayName

            val item = if (arenaEnabled) ItemBuilder(displayItem)
                .name(CC.SECONDARY + CC.B + displayName)
                .lore(CC.GREEN + "Click to disable arena.")
                .build() else ItemStacks.ARENA_DISABLED.name(displayName).build()

            add(item) {
                queueSettings.enableArena(arena, !arenaEnabled)
                reload()
            }
        }

        val slot = addOnNextRow(
            9, ItemStacks.BACK
        ) { viewer.openMenu(prevMenu) }

        addOnRow(slot, 2, ItemStacks.DESELECT_ALL) {
            queueSettings.enabledArenas.clear()
            reload()
        }

        addOnRow(slot, 4, ItemStacks.APPLY, queueInteraction)

        addOnRow(slot, 6, ItemStacks.SELECT_ALL) {
            queuetype.filterArenasByGametype(gametype)
                .forEach { arenaId: Byte -> queueSettings.enableArena(arenaId, true) }
            reload()
        }
    }

    override val title: String
        get() = CC.BLUE + "Toggle Arena"

    override fun shouldUpdate() = true
}
