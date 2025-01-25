package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.inventory.SubmitAction
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.QueuetypeManager.queuetypes
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC

@ClickCancelled(true)
class SelectModeMenu(private val action: SubmitAction) : PracticeMenu() {

    override fun update() {
        setSlot(2, ItemStacks.SIMPLE_MODE) { interaction: Interaction ->
            val p = interaction.profile
            p.resetDuelSettings()
            val gametype by lazy { p.duelSettings.gametype }
            val queuetype by lazy { p.duelSettings.queuetype ?: queuetypes.values.first { it.unranked } }
            p.openMenu(
                SelectExistingKitMenu(
                    QueueArenaEnableMenu(
                        queuetype?.let { gametype?.let { it1 -> it.filterArenasByGametype(it1) } ?: queuetype.arenas }
                            ?: gametype?.arenas ?: arenas.keys,
                        { action.execute(viewer) },
                        this
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
