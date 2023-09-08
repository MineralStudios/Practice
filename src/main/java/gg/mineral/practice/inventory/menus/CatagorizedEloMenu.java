package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.ProfileData;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class CatagorizedEloMenu extends PracticeMenu {
    Catagory catagory;
    Queuetype queuetype;
    ProfileData arg;

    public CatagorizedEloMenu(ProfileData arg, Queuetype queuetype, Catagory catagory) {
        super(CC.BLUE + catagory.getDisplayName());
        this.catagory = catagory;
        this.queuetype = queuetype;
        this.arg = arg;
        setClickCancelled(true);
    }

    @Override
    public boolean update() {

        for (Gametype gametype : catagory.getGametypes()) {
            ItemStack item = new ItemBuilder(gametype.getDisplayItem())
                    .name(gametype.getDisplayName())
                    .lore(CC.ACCENT + arg.getName() + "'s Elo: " + gametype.getElo(arg)).build();
            setSlot(queuetype.getGametypes().getInt(gametype), item);
        }

        return true;
    }
}
