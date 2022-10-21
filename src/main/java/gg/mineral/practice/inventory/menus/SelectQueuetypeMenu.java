package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;

public class SelectQueuetypeMenu implements InventoryBuilder {
    final static String TITLE = CC.BLUE + "Select Queue";

    public SelectQueuetypeMenu() {
        super(TITLE);
        setItemDragging(true);
    }

    @Override
    public MineralInventory build(Profile profile) {
        clear();
        for (Queuetype q : QueuetypeManager.list()) {
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
