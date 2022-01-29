package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.managers.GametypeManager;
import ms.uk.eclipse.tournaments.Tournament;

public class SelectTournamentMenu extends Menu {

    final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();

    public SelectTournamentMenu() {
        super(new StrikingMessage("Select Tournament", CC.PRIMARY, true));
    }

    @Override
    public boolean update() {
        for (Gametype g : gametypeManager.getGametypes()) {
            if (!g.getEvent()) {
                continue;
            }

            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(new ChatMessage(g.getDisplayName(), CC.WHITE, true).toString()).lore().build();

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
