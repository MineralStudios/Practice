package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.tasks.MenuTask;

public class SelectExistingKitMenu extends PracticeMenu {
    final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
    final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();
    PracticeMenu menu;
    boolean simple = false;
    final static String TITLE = CC.BLUE + "Select Existing Kit";

    public SelectExistingKitMenu(PracticeMenu menu, boolean simple) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
        this.simple = simple;
    }

    @Override
    public boolean update() {
        clear();

        for (Gametype g : gametypeManager.getGametypes()) {
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

        for (Catagory c : catagoryManager.getCatagorys()) {
            ItemStack item = new ItemBuilder(c.getDisplayItem())
                    .name(c.getDisplayName()).build();
            add(item, new MenuTask(new SelectCategorizedExistingKitMenu(c, menu, simple)));
        }

        return true;
    }
}
