package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class CatagorizedLeaderboardMenu extends PracticeMenu {
    Catagory catagory;

    public CatagorizedLeaderboardMenu(Catagory catagory) {
        super(CC.BLUE + catagory.getDisplayName());
        this.catagory = catagory;
    }

    @Override
    public boolean update() {
        for (Gametype gametype : catagory.getGametypes()) {
            ItemBuilder itemBuild = new ItemBuilder(gametype.getDisplayItem())
                    .name(gametype.getDisplayName());
            ItemStack item = itemBuild.build();
            ItemMeta meta = item.getItemMeta();

            try {
                meta.setLore(gametype.getLeaderboardLore());
            } catch (Exception e) {
                meta.setLore(null);
            }

            item.setItemMeta(meta);

            add(item);
        }

        return true;
    }
}
