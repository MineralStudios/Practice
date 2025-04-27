package gg.mineral.practice.inventory.menus

import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.PartyManager
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC

@ClickCancelled(true)
class OtherPartiesMenu : PracticeMenu() {
    override fun update() {
        clear()

        for (party in PartyManager.parties.values) {
            if (party.partyLeader.playerStatus === PlayerStatus.IDLE && party != viewer.party) {
                val partyLeader = party.partyLeader
                add(
                    ItemStacks.OTHER_PARTY
                        .name(CC.SECONDARY + CC.B + partyLeader.name).build()
                ) { interaction: Interaction ->
                    interaction.profile.player
                        ?.performCommand("duel " + partyLeader.name)
                }
            }
        }
    }

    override val title: String
        get() = CC.BLUE + "Other Parties"

    override fun shouldUpdate() = true
}
