package gg.mineral.practice.inventory.menus;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.QueuetypeManager;

import gg.mineral.practice.queue.QueueSettings.QueueEntry;
import gg.mineral.practice.queue.QueueSystem;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

@ClickCancelled(true)
public class QueueManagerMenu extends PracticeMenu {

    @Override
    public void update() {
        clear();
        List<QueueEntry> queueEntries = QueueSystem.getQueueEntries(viewer);

        if (queueEntries == null)
            return;

        for (QueueEntry queueEntry : queueEntries) {
            Gametype g = GametypeManager.getGametypes().get(queueEntry.gametype().getId());
            Queuetype q = QueuetypeManager.getQueuetypes().get(queueEntry.queuetype().getId());
            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + q.getDisplayName() + " " + g.getDisplayName())
                    .lore(CC.RED + "Click to leave queue.").build();

            add(item, interaction -> {
                Profile p = interaction.getProfile();
                p.removeFromQueue(q, g);

                if (p.getPlayerStatus() == PlayerStatus.QUEUEING)
                    reload();
            });
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Queue Manager";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
