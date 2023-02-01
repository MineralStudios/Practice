package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class LeaderboardMenu extends PracticeMenu {

    final static String TITLE = CC.BLUE + "Leaderboards";

    public LeaderboardMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        for (Gametype gametype : GametypeManager.getGametypes()) {

            if (gametype.isInCatagory())
                continue;

            ItemStack item = new ItemBuilder(gametype.getDisplayItem())
                    .name(gametype.getDisplayName()).build();
            ItemMeta meta = item.getItemMeta();

            try {
                meta.setLore(gametype.getLeaderboardLore());
            } catch (Exception e) {
                meta.setLore(null);
            }

            item.setItemMeta(meta);
            add(item);
        }

        for (Catagory c : CatagoryManager.getCatagories()) {
            ItemStack item = new ItemBuilder(c.getDisplayItem())
                    .name(c.getDisplayName()).build();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(null);
            item.setItemMeta(meta);
            add(item, p -> {
                p.openMenu(new CatagorizedLeaderboardMenu(c));
                return true;
            });
        }

        return true;
    }
}
