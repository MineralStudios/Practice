package gg.mineral.practice.inventory.menus;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.QueueSearchTask2v2;
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
        clear();
        List<QueueEntry> queueEntries = viewer.getMatchData().getTeam2v2() ? QueueSearchTask2v2.getQueueEntries(viewer)
                : QueueSearchTask.getQueueEntries(viewer);

        if (queueEntries == null) {
            return false;
        }

        for (QueueEntry queueEntry : queueEntries) {
            Gametype g = queueEntry.getGametype();
            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(queueEntry.getQueuetype().getDisplayName() + " " + g.getDisplayName())
                    .lore(CC.RED + "Click to leave queue.").build();

            add(item, interaction -> {
                Profile p = interaction.getProfile();
                p.removeFromQueue(queueEntry);

                if (p.getPlayerStatus() == PlayerStatus.QUEUEING) {
                    reload();
                }
            });
        }
        return true;
    }
}
