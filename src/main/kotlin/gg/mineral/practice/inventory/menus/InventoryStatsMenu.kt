package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.match.data.MatchStatisticCollector
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import kotlin.math.max

@ClickCancelled(true)
class InventoryStatsMenu(val opponent: String? = null, val matchStatisticCollector: MatchStatisticCollector) :
    PracticeMenu() {
    var previousMenu: Menu? = null

    override fun update() {
        if (opponent != null && previousMenu == null) setSlot(
            53, ItemStacks.VIEW_OPPONENT_INVENTORY
        ) { interaction: Interaction ->
            interaction.profile.player
                ?.performCommand("viewinventory $opponent")
        }

        setContents(matchStatisticCollector.inventoryContents)
        matchStatisticCollector.helmet?.let { setSlot(36, it) }
        matchStatisticCollector.chestplate?.let { setSlot(37, it) }
        matchStatisticCollector.leggings?.let { setSlot(38, it) }
        matchStatisticCollector.boots?.let { setSlot(39, it) }

        previousMenu?.let {
            setSlot(45, ItemStacks.BACK) { interaction: Interaction ->
                interaction.profile.openMenu(it)
            }
        }

        setSlot(
            if (previousMenu != null) 48 else 45, if (!matchStatisticCollector.alive)
                ItemStacks.NO_HEALTH
            else
                ItemStacks.HEALTH
                    .name(CC.SECONDARY + CC.B + "Health")
                    .lore(
                        " ", CC.WHITE + "Remaining:",
                        CC.GOLD + matchStatisticCollector.remainingHealth
                    )
                    .amount(matchStatisticCollector.remainingHealth).build()
        )

        setSlot(
            if (previousMenu != null) 49 else 46, ItemStacks.HEALTH_POTIONS_LEFT
                .lore(
                    " ", CC.WHITE + "Thrown: " + CC.GOLD + matchStatisticCollector.potionsThrown,
                    (CC.WHITE + "Missed: " + CC.GOLD
                            + matchStatisticCollector.potionsMissed),
                    (CC.WHITE + "Stolen: " + CC.GOLD
                            + matchStatisticCollector.potionsStolen),
                    (CC.WHITE + "Accuracy: " + CC.GOLD
                            + matchStatisticCollector.potionAccuracy + "%")
                )
                .amount(max(matchStatisticCollector.potionsRemaining.toDouble(), 1.0).toInt()).build()
        )

        setSlot(
            if (previousMenu != null) 50 else 47, ItemStacks.SOUP_LEFT
                .amount(max(matchStatisticCollector.soupsRemaining.toDouble(), 1.0).toInt()).build()
        )

        setSlot(
            if (previousMenu != null) 51 else 48, ItemStacks.HITS
                .name(CC.SECONDARY + CC.B + matchStatisticCollector.hitCount + " Hits")
                .lore(
                    (CC.WHITE + "Longest Combo: " + CC.GOLD
                            + matchStatisticCollector.longestCombo),
                    (CC.WHITE + "Average Combo: " + CC.GOLD
                            + matchStatisticCollector.averageCombo),
                    (CC.WHITE + "W Tap Accuracy: " + CC.GOLD
                            + matchStatisticCollector.wTapAccuracy + "%")
                )
                .build()
        )

        setSlot(
            if (previousMenu != null) 52 else 49, ItemStacks.CLICKS
                .name(CC.SECONDARY + CC.B + matchStatisticCollector.totalClicks + " Clicks")
                .lore(
                    (CC.WHITE + "Highest CPS: " + CC.GOLD
                            + matchStatisticCollector.highestCps),
                    (CC.WHITE + "Average CPS: " + CC.GOLD
                            + matchStatisticCollector.averageCps),
                    (CC.WHITE + "CPS Deviation: " + CC.GOLD
                            + "±" + matchStatisticCollector.cpsDeviation)
                )
                .build()
        )

        setSlot(
            if (previousMenu != null) 53 else 50, ItemStacks.POTION_EFFECTS
                .lore(*matchStatisticCollector.potionEffectStringArray).build()
        )
    }

    override val title: String
        get() = CC.BLUE + matchStatisticCollector.profile.name

    override fun shouldUpdate() = false
}
