package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.tournaments.Tournament;

public class SelectTournamentMenu implements InventoryBuilder {

    final static String TITLE = CC.BLUE + "Select Tournament";

    public SelectTournamentMenu() {
        super(TITLE);
    }

    @Override
    public MineralInventory build(Profile profile) {
        for (Gametype g : GametypeManager.list()) {
            if (!g.getEvent()) {
                continue;
            }

            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(g.getDisplayName()).lore().build();

            Runnable runnable = () -> {
                viewer.bukkit().closeInventory();

                viewer.getMatchData().setGametype(g);

                if (g.getEventArena() == null) {
                    return;
                }

                Tournament t = new Tournament(viewer, g.getEventArena());
                t.start();
            };

            add(item, runnable);
        }

        return true;
    }
}
