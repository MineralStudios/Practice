package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class QueueManagerMenu extends PracticeMenu {
    final static String TITLE = CC.BLUE + "Queue Manager";

    public QueueManagerMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        for (QueueEntry queueEntry : QueueSearchTask.getQueueEntries(viewer)) {
            Gametype g = queueEntry.getGametype();
            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(queueEntry.getQueuetype().getDisplayName() + " " + g.getDisplayName())
                    .lore(CC.RED + "Click to leave queue.").build();

            add(item, p -> {
                p.removeFromQueue(queueEntry);
                p.openMenu(this);
                return true;
            });
        }
        return true;
    }
}
