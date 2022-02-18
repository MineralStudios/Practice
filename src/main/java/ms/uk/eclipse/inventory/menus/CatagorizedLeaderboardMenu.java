package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.PracticeMenu;

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
