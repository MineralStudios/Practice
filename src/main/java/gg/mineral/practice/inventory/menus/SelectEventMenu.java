package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.events.Event;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

@ClickCancelled(true)
public class SelectEventMenu extends PracticeMenu {
    @Override
    public void update() {
        for (Gametype g : GametypeManager.getGametypes().values()) {
            if (!g.isEvent())
                continue;

            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(CC.SECONDARY + CC.B
                            + (g.isInCatagory() ? g.getCatagoryName() + g.getDisplayName() : g.getDisplayName()))
                    .lore(CC.ACCENT + "Click to start event.")
                    .build();

            add(item, () -> {
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
