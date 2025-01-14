package gg.mineral.practice.inventory.menus

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
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
    protected val menu: PracticeMenu,
    protected val simple: Boolean,
    private val prevMenu: PracticeMenu? = null,
) : PracticeMenu() {

    override fun update() {
        clear()

        for (g in GametypeManager.gametypes.values) {
            if (g.inCategory) continue
            val item: ItemStack = ItemBuilder(g.displayItem)
                .name(CC.SECONDARY + CC.B + g.displayName).lore(CC.ACCENT + "Click to select.").build()

            addAfter(9, item) {
                if (simple) viewer.duelSettings.gametype = g
                else viewer.duelSettings.kit = g.kit
                viewer.openMenu(menu)
            }
        }

        for (c in CategoryManager.categories.values) {
            val itemBuild: ItemBuilder = ItemBuilder(c.displayItem)
                .name(CC.SECONDARY + CC.B + c.displayName)

            val sb: GlueList<String> = GlueList<String>()
            sb.add(CC.SECONDARY + "Includes:")

            c.gametypes.map { GametypeManager.gametypes[it] }
                .forEach { sb.add(CC.WHITE + it.displayName) }

            sb.add(" ")
            sb.add(CC.BOARD_SEPARATOR)
            sb.add(CC.ACCENT + "Click to view category.")

            itemBuild.lore(*sb.toTypedArray<String>())
            val item: ItemStack = itemBuild.build()
            addAfter(9, item) { interaction: Interaction ->
                interaction.profile
                    .openMenu(SelectCategorizedExistingKitMenu(c, menu, simple))
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
        ) { interaction: Interaction ->
            interaction.profile.duelSettings.oldCombat = !oldCombat
            interaction.profile.duelSettings.knockback = OldStyleKnockback()
            reload()
        }
    }

    override val title: String
        get() = CC.BLUE + "Select Existing Kit"

    override fun shouldUpdate() = true
}
