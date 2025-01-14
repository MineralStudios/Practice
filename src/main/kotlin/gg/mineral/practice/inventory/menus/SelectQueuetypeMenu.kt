package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.QueuetypeManager.queuetypes
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.messages.CC
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@ClickCancelled(true)
class SelectQueuetypeMenu(private val type: SelectGametypeMenu.Type) : PracticeMenu() {

    override fun update() {
        clear()
        setSlot(18, ItemStack(Material.AIR))
        val queueCount: Int = queuetypes.size
        val horizontalSpacing = 9 / queueCount
        val queuetypeArray = queuetypes.values.toTypedArray<Queuetype>()
        for (i in 0..<queueCount) {
            val q = queuetypeArray[i]
            addAfter(
                11 + i * horizontalSpacing, ItemBuilder(q.displayItem.clone())
                    .name(CC.SECONDARY + CC.B + q.displayName)
                    .lore(
                        CC.ACCENT + (if (type == SelectGametypeMenu.Type.KIT_EDITOR)
                            "Click to edit kit."
                        else
                            "Click to select.")
                    )
                    .build()
            ) { interaction: Interaction ->
                interaction.profile.openMenu(
                    SelectGametypeMenu(
                        q,
                        type, this
                    )
                )
            }
        }
    }

    override val title: String
        get() = CC.BLUE + "Select Queue"

    override fun shouldUpdate() = true
}
