package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.managers.PlayerManager;

public class EloMenu extends Menu {
    GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
    PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
    Profile arg;
    String strArg;

    public EloMenu(Profile arg) {
        super(new StrikingMessage(arg.getName(), CC.PRIMARY, true));
        this.arg = arg;
        setClickCancelled(true);
    }

    public EloMenu(String arg) {
        super(new StrikingMessage(arg, CC.PRIMARY, true));
        strArg = arg;
        setClickCancelled(true);
    }

    public void update() {
        if (arg == null) {
            for (Gametype g : gametypeManager.getGametypes()) {
                ItemStack item = new ItemBuilder(g.getDisplayItem())
                        .name(new ChatMessage(g.getDisplayName(), CC.WHITE, true).toString())
                        .lore(strArg + "'s Elo: " + playerManager.getOfflinePlayerElo(g, strArg)).build();
                add(item);
            }
        } else {
            for (Gametype g : gametypeManager.getGametypes()) {
                ItemStack item = new ItemBuilder(g.getDisplayItem())
                        .name(new ChatMessage(g.getDisplayName(), CC.WHITE, true).toString())
                        .lore(arg.getName() + "'s Elo: " + arg.getElo(g)).build();
                add(item);
            }
        }
    }
}
