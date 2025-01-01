package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.events.Event;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.val;

@ClickCancelled(true)
public class SelectEventMenu extends PracticeMenu {
    @Override
    public void update() {
        for (val g : GametypeManager.getGametypes().values()) {
            if (!g.isEvent())
                continue;

            val item = new ItemBuilder(g.getDisplayItem())
                    .name(CC.SECONDARY + CC.B
                            + (g.isInCategory() ? g.getCategoryName() + g.getDisplayName() : g.getDisplayName()))
                    .lore(CC.ACCENT + "Click to start event.")
                    .build();

            add(item, interaction -> {
                viewer.getPlayer().closeInventory();

                viewer.getDuelSettings().setGametype(g);

                if (g.getEventArenaId() == -1) {
                    viewer.message(ErrorMessages.ARENA_NOT_FOUND);
                    return;
                }

                Event event = new Event(viewer, g.getEventArenaId());
                event.start();
            });
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Select Event";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
