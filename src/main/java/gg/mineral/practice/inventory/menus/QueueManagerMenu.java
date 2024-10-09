package gg.mineral.practice.inventory.menus;

import java.util.List;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.QueueSettings;
import gg.mineral.practice.queue.QueueSystem;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

@ClickCancelled(true)
public class QueueManagerMenu extends PracticeMenu {

    @Override
    public void update() {
        clear();
        List<UUID> queueEntries = QueueSystem.getQueueEntries(viewer);

        if (queueEntries == null)
            return;

        for (UUID uuid : queueEntries) {
            Gametype g = GametypeManager.getGametypes().get(QueueSettings.getGameTypeId(uuid));
            Queuetype q = QueuetypeManager.getQueuetypes().get(QueueSettings.getQueueTypeId(uuid));
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
