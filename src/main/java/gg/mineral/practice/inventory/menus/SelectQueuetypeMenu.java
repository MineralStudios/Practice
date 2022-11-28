package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;

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
                add(item, p -> {
                    p.openMenu(new SelectGametypeMenu(q, false, true));
                    return true;
                });
            } catch (NullPointerException e) {
                continue;
            }
        }

        return true;
    }
}
