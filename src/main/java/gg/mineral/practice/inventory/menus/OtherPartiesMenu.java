package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.val;
import gg.mineral.practice.entity.PlayerStatus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.PartyManager;

@ClickCancelled(true)
public class OtherPartiesMenu extends PracticeMenu {

    @Override
    public void update() {
        clear();

        for (val party : PartyManager.getParties()) {
            if (party.getPartyLeader().getPlayerStatus() == PlayerStatus.IDLE && !party.equals(viewer.getParty())) {
                val partyLeader = party.getPartyLeader();
                add(ItemStacks.OTHER_PARTY
                        .name(CC.SECONDARY + CC.B + partyLeader.getName()).build(),
                        interaction -> interaction.getProfile().getPlayer()
                                .performCommand("duel " + partyLeader.getName()));
            }
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Other Parties";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
