package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.tasks.MenuTask;

public class SelectQueuetypeMenu extends Menu {
    QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();

    public SelectQueuetypeMenu() {
        super(new StrikingMessage("Select Queue", CC.PRIMARY, true));
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        clear();
        for (Queuetype q : queuetypeManager.getQueuetypes()) {
            try {
                ItemStack item = new ItemBuilder(q.getDisplayItem())
                        .name(new ChatMessage(q.getDisplayName(), CC.WHITE, true).toString()).build();
                add(item, new MenuTask(new SelectGametypeMenu(q, false, true)));
            } catch (NullPointerException e) {
                continue;
            }
        }

        return true;
    }
}
