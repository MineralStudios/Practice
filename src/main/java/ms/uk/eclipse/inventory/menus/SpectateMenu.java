package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.managers.MatchManager;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.queue.QueueEntry;

public class SpectateMenu extends PracticeMenu {
    MatchManager matchManager = PracticePlugin.INSTANCE.getMatchManager();
    final static String TITLE = CC.BLUE + "Spectate";

    public SpectateMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        clear();
        for (Match m : matchManager.getMatchs()) {
            QueueEntry queueEntry = m.getData().getQueueEntry();
            ItemStack item = queueEntry == null ? new ItemStack(Material.WOOD_AXE)
                    : m.getData().getQueueEntry().getGametype().getDisplayItem();
            ItemStack skull = new ItemBuilder(item)
                    .name(CC.SECONDARY + m.getPlayer1().getName() + " vs " + m.getPlayer2().getName())
                    .build();
            add(skull, new CommandTask("spec " + m.getParticipants().get(0).getName()));
        }

        return true;
    }
}
