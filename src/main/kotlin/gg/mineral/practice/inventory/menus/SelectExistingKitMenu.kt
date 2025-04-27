package gg.mineral.practice.inventory.menus

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.CategoryManager
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.match.knockback.OldStyleKnockback
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import org.bukkit.inventory.ItemStack

@ClickCancelled(true)
open class SelectExistingKitMenu(
    protected val onSelect: (Gametype) -> Unit,
    private val simple: Boolean,
    private val prevMenu: Menu? = null,
) : PracticeMenu() {

    fun addGametypes(gametypes: Collection<Gametype>) {
        gametypes.forEach {
            val item: ItemStack = ItemBuilder(it.displayItem)
                .name(CC.SECONDARY + CC.B + it.displayName).lore(CC.ACCENT + "Click to select.").build()

            addAfter(9, item) { _ -> onSelect(it) }
        }
    }

    override fun update() {
        clear()

        addGametypes(GametypeManager.gametypes.values.filterNotNull().filter { !it.inCategory })

        for (c in CategoryManager.categories.values) {
            c ?: continue
            val itemBuild: ItemBuilder = ItemBuilder(c.displayItem)
                .name(CC.SECONDARY + CC.B + c.displayName)

            val sb: GlueList<String> = GlueList<String>()
            sb.add(CC.SECONDARY + "Includes:")

            c.gametypes.mapNotNull { GametypeManager.gametypes[it] }
                .forEach { sb.add(CC.WHITE + it.displayName) }

            sb.add(" ")
            sb.add(CC.BOARD_SEPARATOR)
            sb.add(CC.ACCENT + "Click to view category.")

            itemBuild.lore(*sb.toTypedArray<String>())
            val item: ItemStack = itemBuild.build()
            addAfter(9, item) { interaction: Interaction ->
                interaction.profile
                    .openMenu(SelectCategorizedExistingKitMenu(c, onSelect, simple))
            }
        }

        if (prevMenu != null) setSlot(
            if (simple) 39 else 40, ItemStacks.BACK
        ) { viewer.openMenu(prevMenu) }

        val oldCombat = viewer.duelSettings.oldCombat

        if (simple) setSlot(
            41, ItemStacks.OLD_COMBAT.name(CC.SECONDARY + CC.B + "Old Combat Mechanics").lore(
                (CC.WHITE + "Play using " + CC.SECONDARY + "old combat" + CC.WHITE
                        + " seen on servers from 2015-2017."),
                " ",
                CC.WHITE + "Currently:", if (oldCombat) CC.GREEN + "Enabled" else CC.RED + "Disabled", " ",
                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle old combat."
            ).build()
        ) {
            it.profile.duelSettings.oldCombat = !oldCombat
            it.profile.duelSettings.knockback = OldStyleKnockback()
            reload()
        }
    }

    override val title: String
        get() = CC.BLUE + "Select Existing Kit"

    override fun shouldUpdate() = true
}
