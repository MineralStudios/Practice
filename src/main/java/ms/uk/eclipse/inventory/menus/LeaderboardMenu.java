package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.managers.CatagoryManager;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.tasks.MenuTask;

public class LeaderboardMenu extends Menu {
    final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
    final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();

    public LeaderboardMenu() {
        super(new StrikingMessage("Leaderboards", CC.PRIMARY, true));
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        for (Gametype g : gametypeManager.getGametypes()) {

            if (g.isInCatagory()) {
                continue;
            }

            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(new ChatMessage(g.getDisplayName(), CC.WHITE, true).toString()).build();
            ItemMeta meta = item.getItemMeta();

            try {
                meta.setLore(g.getLeaderboardLore());
            } catch (Exception e) {
                meta.setLore(null);
            }

            item.setItemMeta(meta);
            add(item);
        }

        for (Catagory c : catagoryManager.getCatagorys()) {
            ItemStack item = new ItemBuilder(c.getDisplayItem())
                    .name(new ChatMessage(c.getDisplayName(), CC.WHITE, true).toString()).build();
            ItemMeta meta = item.getItemMeta();
            meta.setLore(null);
            item.setItemMeta(meta);
            add(item, new MenuTask(new CatagorizedLeaderboardMenu(c)));
        }

        return true;
    }
}
