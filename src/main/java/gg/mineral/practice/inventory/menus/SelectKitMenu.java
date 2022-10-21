package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.api.inventory.InventoryBuilder;

public class SelectKitMenu implements InventoryBuilder {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Select Kit";

    public SelectKitMenu(MechanicsMenu menu) {
        super(TITLE);
        setItemDragging(true);
        this.menu = menu;
    }

    @Override
    public MineralInventory build(Profile profile) {
        ItemStack item = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .name("Choose Existing Kit").build();
        ItemStack item2 = new ItemBuilder(Material.GOLD_CHESTPLATE)
                .name("Create Custom Kit").build();
        set(2, item, new MenuTask(new SelectExistingKitMenu(menu, false)));
        Runnable runnable = viewer::sendPlayerToKitCreator;
        set(6, item2, runnable);
        return true;
    }
}
