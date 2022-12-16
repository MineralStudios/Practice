package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.ProfileManager;

public class EloMenu extends PracticeMenu {

    Profile arg;
    String strArg;

    public EloMenu(Profile arg) {
        super(CC.BLUE + arg.getName());
        this.arg = arg;
        setClickCancelled(true);
    }

    public EloMenu(String arg) {
        super(CC.BLUE + arg);
        strArg = arg;
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        if (arg == null) {
            for (Gametype gametype : GametypeManager.getGametypes()) {
                ItemStack item = new ItemBuilder(gametype.getDisplayItem())
                        .name(gametype.getDisplayName())
                        .lore(CC.ACCENT + strArg + "'s Elo: " + ProfileManager.getOfflinePlayerElo(gametype, strArg))
                        .build();
                add(item);
            }

            return true;
        }

        for (Gametype gametype : GametypeManager.getGametypes()) {
            ItemStack item = new ItemBuilder(gametype.getDisplayItem())
                    .name(gametype.getDisplayName())
                    .lore(CC.ACCENT + arg.getName() + "'s Elo: " + gametype.getElo(arg)).build();
            add(item);
        }

        return true;
    }
}
