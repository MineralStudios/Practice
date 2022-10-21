package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.GametypeManager;

public class SelectExistingKitMenu implements InventoryBuilder {
    ;
    PracticeMenu menu;
    boolean simple = false;
    final static String TITLE = CC.BLUE + "Select Existing Kit";

    public SelectExistingKitMenu(PracticeMenu menu, boolean simple) {
        super(TITLE);
        setItemDragging(true);
        this.menu = menu;
        this.simple = simple;
    }

    @Override
    public MineralInventory build(Profile profile) {
        clear();

        for (Gametype g : GametypeManager.list()) {
            if (g.isInCatagory())
                continue;
            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(g.getDisplayName()).build();

            Runnable runnable = () -> {
                if (simple) {
                    viewer.getMatchData().setGametype(g);
                } else {
                    viewer.getMatchData().setKit(g.getKit(), g.getName());
                }

                viewer.openMenu(menu);
            };

            add(item, runnable);
        }

        for (Catagory c : CatagoryManager.list()) {
            ItemStack item = new ItemBuilder(c.getDisplayItem())
                    .name(c.getDisplayName()).build();
            add(item, new MenuTask(new SelectCategorizedExistingKitMenu(c, menu, simple)));
        }

        return true;
    }
}
