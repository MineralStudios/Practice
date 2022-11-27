package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;

public class CatagorizedLeaderboardMenu extends PracticeMenu {
    Catagory c;

    public CatagorizedLeaderboardMenu(Catagory c) {
        super(CC.BLUE + c.getDisplayName());
        this.c = c;
    }

    @Override
    public boolean update() {
        for (Gametype g : c.getGametypes()) {
            ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem())
                    .name(g.getDisplayName());
            ItemStack item = itemBuild.build();
            ItemMeta meta = item.getItemMeta();

            try {
                meta.setLore(g.getLeaderboardLore());
            } catch (Exception e) {
                meta.setLore(null);
            }

            item.setItemMeta(meta);

            add(item);
        }

        return true;
    }
}
