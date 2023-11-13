package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class CatagorizedLeaderboardMenu extends PracticeMenu {
    Catagory catagory;
    Queuetype queuetype;

    public CatagorizedLeaderboardMenu(Queuetype queuetype, Catagory catagory) {
        super(CC.BLUE + catagory.getDisplayName());
        this.catagory = catagory;
        this.queuetype = queuetype;
        setClickCancelled(true);
    }

    @Override
    public boolean update() {

        for (Gametype gametype : catagory.getGametypes()) {

            ItemStack item = new ItemBuilder(gametype.getDisplayItem().clone())
                    .name(CC.SECONDARY + CC.B + gametype.getDisplayName()).build();
            ItemMeta meta = item.getItemMeta();

            try {
                meta.setLore(gametype.getLeaderboardLore());
            } catch (Exception e) {
                meta.setLore(null);
            }

            item.setItemMeta(meta);
            setSlot(queuetype.getGametypes().getInt(gametype), item);
        }

        return true;
    }
}
