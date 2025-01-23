package gg.mineral.practice.inventory.menus

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC

@ClickCancelled(true)
class SaveLoadKitsMenu : PracticeMenu() {
    override fun update() {
        if (viewer.playerStatus === PlayerStatus.KIT_CREATOR) {
            setSlot(
                4, ItemStacks.SAVE_KIT
            ) { viewer.kitCreator?.save() }
            return
        }

        val kitEditor = viewer.kitEditor
        val loadouts = viewer.getCustomKits(
            kitEditor!!.queuetype,
            kitEditor.gametype
        )

        for (i in 0..8) {
            if (!loadouts.containsKey(i)) setSlot(
                i, ItemStacks.SAVE_KIT
            ) {
                viewer.kitEditor?.save(i)
                reload()
            }
            else setSlot(
                i, ItemStacks.DELETE_KIT
            ) {
                viewer.kitEditor?.delete(i)
                reload()
            }
        }

        return
    }

    override val title: String
        get() = CC.BLUE + "Save Kits"

    override fun shouldUpdate() = true
}
