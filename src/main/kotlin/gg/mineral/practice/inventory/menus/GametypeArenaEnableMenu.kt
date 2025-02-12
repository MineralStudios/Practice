package gg.mineral.practice.inventory.menus

import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.ArenaManager
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ChatMessages
import org.bukkit.ChatColor

@ClickCancelled(true)
class GametypeArenaEnableMenu(private val gametype: Gametype) : PracticeMenu() {
    private var numberOfArenas = 0

    override fun update() {
        clear()

        val arenas = arenas.values
        numberOfArenas = ArenaManager.arenas.size

        for (a in arenas) {
            a ?: continue
            val arenaEnabled = gametype.arenas.contains(a.id)
            val color = if (arenaEnabled) ChatColor.GREEN else ChatColor.RED

            val item = ItemBuilder(a.displayItem).name(a.displayName).lore(color.toString() + arenaEnabled).build()

            add(item) {
                gametype.enableArena(a, !arenaEnabled)
                ChatMessages.GAMETYPE_ARENA_SET.clone().replace("%gametype%", gametype.name)
                    .replace("%toggled%", "" + !arenaEnabled).replace("%arena%", a.name)
                    .send(viewer.player)
                reload()
            }
        }
    }

    override val title: String
        get() = CC.BLUE + "Toggle Arena"

    override fun shouldUpdate() = numberOfArenas != arenas.size
}
