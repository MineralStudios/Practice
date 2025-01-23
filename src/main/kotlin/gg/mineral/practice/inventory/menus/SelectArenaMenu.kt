package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.*
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.match.TeamMatch
import gg.mineral.practice.match.data.MatchData
import gg.mineral.practice.tournaments.Tournament
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ErrorMessages
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

@ClickCancelled(true)
class SelectArenaMenu(private val menu: Menu, private val prevMenu: Menu, private val action: SubmitAction) :
    PracticeMenu() {
    private val simpleMode = menu !is MechanicsMenu

    override fun update() {
        val arenas = arenas
        val gametype = viewer.duelSettings.gametype
        val arenaIds = if (simpleMode && gametype != null)
            gametype.arenas.iterator()
        else
            arenas.keys.iterator()

        while (arenaIds.hasNext()) {
            val arenaId = arenaIds.nextByte()

            val arena = arenas[arenaId]

            val item: ItemStack
            try {
                item = ItemBuilder(arena.displayItem.clone())
                    .name(CC.SECONDARY + CC.B + arena.displayName).lore(CC.ACCENT + "Click to select.")
                    .build()
            } catch (e: Exception) {
                continue
            }

            var arenaRunnable =
                Consumer<Interaction> { _: Interaction ->
                    viewer.duelSettings.arenaId = arenaId
                    if (simpleMode) {
                        viewer.player.closeInventory()
                        viewer.duelRequestReciever?.let { viewer.sendDuelRequest(it) }
                        return@Consumer
                    }
                    viewer.openMenu(menu)
                }

            if (action === SubmitAction.P_SPLIT && simpleMode) {
                arenaRunnable = Consumer {
                    viewer.player.closeInventory()
                    viewer.duelSettings.arenaId = arenaId

                    val p = viewer.party

                    if (!viewer.party!!.partyLeader.equals(viewer)) {
                        viewer.message(ErrorMessages.YOU_ARE_NOT_PARTY_LEADER)
                        return@Consumer
                    }

                    if (p!!.partyMembers.size < 2) {
                        viewer.message(ErrorMessages.PARTY_NOT_BIG_ENOUGH)
                        return@Consumer
                    }

                    val m = TeamMatch(p, MatchData(viewer.duelSettings))
                    m.start()
                }
            } else if (action === SubmitAction.TOURNAMENT && simpleMode) {
                arenaRunnable = Consumer<Interaction> {
                    viewer.player.closeInventory()
                    viewer.duelSettings.arenaId = arenaId

                    val tournament = Tournament(viewer)
                    tournament.start()
                }
            }

            addAfter(9, item, arenaRunnable)
        }

        addOnNextRow(
            13, ItemStacks.BACK
        ) { viewer.openMenu(prevMenu) }
    }

    override val title: String
        get() = CC.BLUE + "Select Arena"

    override fun shouldUpdate() = true
}
