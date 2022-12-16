package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.GametypeManager;

public class SelectExistingKitMenu extends PracticeMenu {

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

        for (Gametype g : GametypeManager.getGametypes()) {
            if (g.isInCatagory())
                continue;
            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(g.getDisplayName()).lore().build();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if (simple) {
                        viewer.getMatchData().setGametype(g);
                    } else {
                        viewer.getMatchData().setKit(g.getKit());
                    }

                    viewer.openMenu(menu);
                }
            };

            add(item, runnable);
        }

        for (Catagory c : CatagoryManager.getCatagories()) {
            ItemStack item = new ItemBuilder(c.getDisplayItem())
                    .name(c.getDisplayName()).build();
            add(item, p -> {
                p.openMenu(new SelectCategorizedExistingKitMenu(c, menu, simple));
                return true;
            });
        }

        return true;
    }
}
