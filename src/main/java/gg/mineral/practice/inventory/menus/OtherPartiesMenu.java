package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.party.Party;

public class OtherPartiesMenu extends PracticeMenu {

    final static String TITLE = CC.BLUE + "Other Parties";

    public OtherPartiesMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        clear();

        for (Party party : PartyManager.getParties()) {
            if (party.getPartyLeader().getPlayerStatus() == PlayerStatus.IDLE && !party.equals(viewer.getParty())) {
                Profile partyLeader = party.getPartyLeader();
                add(ItemStacks.OTHER_PARTY
                        .name(partyLeader.getName()).build(),
                        interaction -> {
                            Profile p = interaction.getProfile();
                            p.getPlayer().performCommand("duel " + partyLeader.getName());
                        });
            }
        }

        return true;
    }
}
