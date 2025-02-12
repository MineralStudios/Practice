package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import it.unimi.dsi.fastutil.bytes.ByteSet
import java.util.function.Consumer

@ClickCancelled(true)
class QueueArenaEnableMenu(
    private val arenas: ByteSet,
    private val queueInteraction: Consumer<Interaction>,
    private val prevMenu: Menu
) : PracticeMenu() {

    override fun update() {
        clear()
        val iter = arenas.iterator()
        val queueSettings = viewer.queueSettings
        val duelSettings = viewer.duelSettings

        while (iter.hasNext()) {
            val arenaId = iter.nextByte()
            val arenaEnabled = queueSettings.enabledArenas[arenaId] && duelSettings.enabledArenas[arenaId]

            val arena = ArenaManager.arenas[arenaId] ?: continue

            val displayName = arena.displayName

            val item = if (arenaEnabled) ItemStacks.ARENA_ENABLED.name(CC.SECONDARY + CC.B + displayName)
                .build() else ItemStacks.ARENA_DISABLED.name(CC.SECONDARY + CC.B + displayName).build()

            addAfter(9, item) {
                if (it.clickType.isRightClick) arena.spectateArena(viewer)
                else {
                    val shouldEnable = !arenaEnabled
                    queueSettings.enableArena(arena, shouldEnable)
                    duelSettings.enableArena(arena, shouldEnable)
                    reload()
                }
            }
        }

        val slot = addOnNextRow(
            9, ItemStacks.BACK
        ) { viewer.openMenu(prevMenu) }

        addOnRow(slot, 2, ItemStacks.DESELECT_ALL) {
            queueSettings.enabledArenas.clear()
            duelSettings.enabledArenas.clear()
            reload()
        }

        addOnRow(slot, 4, ItemStacks.APPLY, queueInteraction)

        addOnRow(slot, 6, ItemStacks.SELECT_ALL) {
            this.arenas.forEach {
                queueSettings.enableArena(it, true)
                duelSettings.enableArena(it, true)
            }
            reload()
        }
    }

    override val title: String
        get() = CC.BLUE + "Toggle Arena"

    override fun shouldUpdate() = true
}
