package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.events.Event;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class SelectEventMenu extends PracticeMenu {

    final static String TITLE = CC.BLUE + "Select Event";

    public SelectEventMenu() {
        super(TITLE);
    }

    @Override
    public boolean update() {
        for (Gametype g : GametypeManager.getGametypes()) {
            if (!g.getEvent()) {
                continue;
            }

            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(g.getDisplayName()).lore().build();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    viewer.getPlayer().closeInventory();

                    viewer.getMatchData().setGametype(g);

                    if (g.getEventArena() == null) {
                        viewer.message(ErrorMessages.ARENA_NOT_FOUND);
                        return;
                    }

                    Event event = new Event(viewer, g.getEventArena());
                    event.start();
                }
            };

            add(item, runnable);
        }

        return true;
    }
}
