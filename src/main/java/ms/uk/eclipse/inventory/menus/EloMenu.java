package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.PlayerManager;

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
