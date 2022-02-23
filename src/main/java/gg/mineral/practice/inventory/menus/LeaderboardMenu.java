package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.tasks.MenuTask;

public class LeaderboardMenu extends PracticeMenu {
    final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
    final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();
    final static String TITLE = CC.BLUE + "Leaderboards";

    public LeaderboardMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        for (Gametype g : gametypeManager.getGametypes()) {

            if (g.isInCatagory()) {
                continue;
            }

            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(g.getDisplayName()).build();
            ItemMeta meta = item.getItemMeta();

            try {
                meta.setLore(g.getLeaderboardLore());
            } catch (Exception e) {
                meta.setLore(null);
            }

            item.setItemMeta(meta);
            add(item);
        }

        for (Catagory c : catagoryManager.getCatagorys()) {
            ItemStack item = new ItemBuilder(c.getDisplayItem())
                    .name(c.getDisplayName()).build();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(null);
            item.setItemMeta(meta);
            add(item, new MenuTask(new CatagorizedLeaderboardMenu(c)));
        }

        return true;
    }
}
