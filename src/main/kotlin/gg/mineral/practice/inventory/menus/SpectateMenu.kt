package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.MatchManager.matches
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC

@ClickCancelled(true)
class SpectateMenu : PracticeMenu() {
    override fun update() {
        clear()
        for (m in matches) {
            val gametype = m.data.gametype

            if (m.profile1 == null || m.profile2 == null) continue


            val item = gametype?.displayItem?.clone() ?: ItemStacks.LOAD_KIT.build()

            val skull = ItemBuilder(item.clone())
                .name(CC.SECONDARY + CC.B + m.profile1?.name + " vs " + m.profile2?.name)
                .lore(
                    CC.WHITE + "Game type:",
                    CC.GOLD + (gametype?.name ?: "Custom"),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to spectate."
                )
                .build()
            add(skull) { interaction: Interaction -> m.participants.first?.let { interaction.profile.spectate(it) } }
        }
    }

    override val title: String
        get() = CC.BLUE + "Spectate"

    override fun shouldUpdate() = true
}
