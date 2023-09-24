package gg.mineral.practice.inventory.menus;

import java.util.List;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class InventoryStatsListMenu extends PracticeMenu {
    List<InventoryStatsMenu> list;

    final static String TITLE = CC.BLUE + "Inventories";

    public InventoryStatsListMenu(List<InventoryStatsMenu> list) {
        super(TITLE);
        setClickCancelled(true);
        this.list = list;
    }

    @Override
    public boolean update() {

        for (InventoryStatsMenu inventoryStatsMenu : list) {
            add(ItemStacks.INVENTORY_STATS.name(CC.SECONDARY + CC.B + inventoryStatsMenu.getTitle())
                    .lore(CC.ACCENT + "Click to view.").build(), interaction -> {
                        Profile p = interaction.getProfile();
                        p.openMenu(inventoryStatsMenu);
                    });
        }
        return true;
    }
}
