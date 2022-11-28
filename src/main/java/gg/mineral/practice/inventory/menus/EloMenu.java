package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.PlayerManager;

public class EloMenu extends PracticeMenu {
    GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
    PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
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
            for (Gametype g : gametypeManager.getGametypes()) {
                ItemStack item = new ItemBuilder(g.getDisplayItem())
                        .name(g.getDisplayName())
                        .lore(CC.ACCENT + strArg + "'s Elo: " + playerManager.getOfflinePlayerElo(g, strArg)).build();
                add(item);
            }

            return true;
        }

        for (Gametype g : gametypeManager.getGametypes()) {
            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(g.getDisplayName())
                    .lore(CC.ACCENT + arg.getName() + "'s Elo: " + g.getElo(arg)).build();
            add(item);
        }

        return true;
    }
}
