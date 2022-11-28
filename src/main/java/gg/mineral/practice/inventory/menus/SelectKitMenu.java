package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;

public class SelectKitMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Select Kit";

    public SelectKitMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        ItemStack item = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .name("Choose Existing Kit").build();
        ItemStack item2 = new ItemBuilder(Material.GOLD_CHESTPLATE)
                .name("Create Custom Kit").build();
        setSlot(2, item, p -> {
            p.openMenu(new SelectExistingKitMenu(menu, false));
            return true;
        });
        Runnable runnable = viewer::sendPlayerToKitCreator;
        setSlot(6, item2, runnable);
        return true;
    }
}
