package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.tournaments.Tournament;

public class SelectTournamentMenu extends PracticeMenu {

    final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
    final static String TITLE = CC.BLUE + "Select Tournament";

    public SelectTournamentMenu() {
        super(TITLE);
    }

    @Override
    public boolean update() {
        for (Gametype g : gametypeManager.getGametypes()) {
            if (!g.getEvent()) {
                continue;
            }

            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(g.getDisplayName()).lore().build();

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    viewer.bukkit().closeInventory();

                    viewer.getMatchData().setGametype(g);

                    if (g.getEventArena() == null) {
                        return;
                    }

                    Tournament t = new Tournament(viewer, g.getEventArena());
                    t.start();
                }
            };

            add(item, runnable);
        }

        return true;
    }
}
