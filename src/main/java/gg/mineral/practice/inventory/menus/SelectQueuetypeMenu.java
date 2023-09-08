package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class SelectQueuetypeMenu extends PracticeMenu {

    final static String TITLE = CC.BLUE + "Select Queue";
    SelectGametypeMenu.Type type;

    public SelectQueuetypeMenu(SelectGametypeMenu.Type type) {
        super(TITLE);
        setClickCancelled(true);
        this.type = type;
    }

    @Override
    public boolean update() {
        clear();
        for (Queuetype q : QueuetypeManager.getQueuetypes()) {
            try {
                ItemStack item = new ItemBuilder(q.getDisplayItem().clone())
                        .name(CC.SECONDARY + q.getDisplayName()).build();
                add(item, interaction -> {
                    Profile p = interaction.getProfile();
                    p.openMenu(new SelectGametypeMenu(q, type));
                });
            } catch (NullPointerException e) {
                continue;
            }
        }

        return true;
    }
}
