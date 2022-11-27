package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.tasks.MenuTask;

public class SelectQueuetypeMenu extends PracticeMenu {
    QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
    final static String TITLE = CC.BLUE + "Select Queue";

    public SelectQueuetypeMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        clear();
        for (Queuetype q : queuetypeManager.getQueuetypes()) {
            try {
                ItemStack item = new ItemBuilder(q.getDisplayItem())
                        .name(q.getDisplayName()).build();
                add(item, new MenuTask(new SelectGametypeMenu(q, false, true)));
            } catch (NullPointerException e) {
                continue;
            }
        }

        return true;
    }
}
