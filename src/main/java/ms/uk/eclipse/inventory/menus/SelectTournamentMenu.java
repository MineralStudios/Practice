package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.tournaments.Tournament;

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
