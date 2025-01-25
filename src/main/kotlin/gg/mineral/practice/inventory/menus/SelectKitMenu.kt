package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC

@ClickCancelled(true)
class SelectKitMenu(private val menu: MechanicsMenu) : PracticeMenu() {

    override fun update() {
        setSlot(
            11, ItemStacks.CHOOSE_EXISTING_KIT
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                SelectExistingKitMenu(
                    menu, false, menu
                )
            )
        }

        setSlot(15, ItemStacks.CHOOSE_CUSTOM_KIT) { interaction: Interaction ->
            val viewer = interaction.profile
            viewer.player.closeInventory()
            viewer.sendToKitCreator(menu.submitAction)
        }

        setSlot(
            31, ItemStacks.BACK
        ) { it.profile.openMenu(menu) }
    }

    override val title: String
        get() = CC.BLUE + "Select Kit"

    override fun shouldUpdate() = true
}
