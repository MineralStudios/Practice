package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.inventory.SubmitAction
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC

@ClickCancelled(true)
class SelectModeMenu(private val action: SubmitAction) : PracticeMenu() {

    override fun update() {
        setSlot(2, ItemStacks.SIMPLE_MODE) { interaction: Interaction ->
            val p = interaction.profile
            p.resetDuelSettings()
            p.openMenu(
                SelectExistingKitMenu(
                    SelectArenaMenu(
                        this,
                        this,
                        action
                    ), true, this
                )
            )
        }

        setSlot(
            6, ItemStacks.ADVANCED_MODE
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                MechanicsMenu(
                    this,
                    action
                )
            )
        }
    }

    override val title: String
        get() = CC.BLUE + "Select Mode"

    override fun shouldUpdate() = true
}
