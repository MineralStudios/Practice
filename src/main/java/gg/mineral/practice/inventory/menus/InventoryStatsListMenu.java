package gg.mineral.practice.inventory.menus;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemBuilder;
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
            ItemStack item = new ItemBuilder(Material.CHEST)
                    .name(inventoryStatsMenu.getTitle()).build();

            add(item, p -> {
                p.openMenu(inventoryStatsMenu);
                return true;
            });
        }
        return true;
    }
}
