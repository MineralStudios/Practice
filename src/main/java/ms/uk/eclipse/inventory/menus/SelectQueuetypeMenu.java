package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.tasks.MenuTask;

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
