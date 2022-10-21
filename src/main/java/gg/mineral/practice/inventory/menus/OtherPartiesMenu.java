package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.practice.managers.PartyManager;
import gg.mineral.practice.party.Party;

public class OtherPartiesMenu implements InventoryBuilder {
    final static String TITLE = CC.BLUE + "Other Parties";

    public OtherPartiesMenu() {
        super(TITLE);
        setItemDragging(true);
    }

    @Override
    public MineralInventory build(Profile profile) {
        clear();

        for (Party party : PartyManager.list()) {
            if (party.getPartyLeader().getPlayerStatus() == PlayerStatus.IN_LOBBY && !party.equals(viewer.getParty())) {
                Profile partyLeader = party.getPartyLeader();
                ItemStack skull = new ItemBuilder(Material.SKULL_ITEM)
                        .name(partyLeader.getName()).build();
                add(skull, new CommandTask("duel " + partyLeader.getName()));
            }
        }

        return true;
    }
}
