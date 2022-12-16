package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
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

        for (Party p : PartyManager.getParties()) {
            if (p.getPartyLeader().getPlayerStatus() == PlayerStatus.IN_LOBBY && !p.equals(viewer.getParty())) {
                Profile partyLeader = p.getPartyLeader();
                ItemStack skull = new ItemBuilder(Material.SKULL_ITEM)
                        .name(partyLeader.getName()).build();
                add(skull, pr -> pr.getPlayer().performCommand("duel " + partyLeader.getName()));
            }
        }

        return true;
    }
}
