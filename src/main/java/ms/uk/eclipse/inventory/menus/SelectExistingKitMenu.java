package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

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

public class SelectExistingKitMenu extends Menu {
    final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
    final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();
    Menu menu;
    boolean simple = false;

    public SelectExistingKitMenu(Menu menu, boolean simple) {
        super(new StrikingMessage("Select Existing Kit", CC.PRIMARY, true));
        setClickCancelled(true);
        this.menu = menu;
        this.simple = simple;
    }

    @Override
    public boolean update() {
        clear();

        for (Gametype g : gametypeManager.getGametypes()) {
            if (g.isInCatagory())
                continue;
            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(new ChatMessage(g.getDisplayName(), CC.WHITE, true).toString()).build();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    if (simple) {
                        viewer.getMatchData().setGametype(g);
                    } else {
                        viewer.getMatchData().setKit(g.getKit(), g.getName());
                    }

                    viewer.openMenu(menu);
                }
            };

            add(item, runnable);
        }

        for (Catagory c : catagoryManager.getCatagorys()) {
            ItemStack item = new ItemBuilder(c.getDisplayItem())
                    .name(new ChatMessage(c.getDisplayName(), CC.WHITE, true).toString()).build();
            add(item, new MenuTask(new SelectCategorizedExistingKitMenu(c, menu, simple)));
        }

        return true;
    }
}
