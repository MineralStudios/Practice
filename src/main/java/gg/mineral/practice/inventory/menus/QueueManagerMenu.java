package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.PlayerStatus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.QueuetypeManager;

import gg.mineral.practice.queue.QueueSystem;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import lombok.val;

@ClickCancelled(true)
public class QueueManagerMenu extends PracticeMenu {

    @Override
    public void update() {
        clear();
        val queueEntries = QueueSystem.getQueueEntries(viewer);

        for (val queueEntry : queueEntries) {
            val g = GametypeManager.getGametypes().get(queueEntry.gametype().getId());
            val q = QueuetypeManager.getQueuetypes().get(queueEntry.queuetype().getId());

            val categoryName = g.getCategoryName() != null ? g.getCategoryName() + " " : "";

            val item = new ItemBuilder(g.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + q.getDisplayName() + " " + categoryName + g.getDisplayName())
                    .lore(CC.RED + "Click to leave queue.").build();

            add(item, interaction -> {
                val p = interaction.getProfile();
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
