package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.managers.MatchManager;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.queue.QueueEntry;

public class SpectateMenu extends Menu {
    MatchManager matchManager = PracticePlugin.INSTANCE.getMatchManager();

    public SpectateMenu() {
        super(new StrikingMessage("Spectate", CC.PRIMARY, true));
        setClickCancelled(true);
    }

    @Override
    public void update() {
        clear();
        for (Match m : matchManager.getMatchs()) {
            QueueEntry queueEntry = m.getData().getQueueEntry();
            ItemStack item = queueEntry == null ? new ItemStack(Material.WOOD_AXE)
                    : m.getData().getQueueEntry().getGametype().getDisplayItem();
            ItemStack skull = new ItemBuilder(item).name(
                    new ChatMessage(m.getPlayer1().getName() + " vs " + m.getPlayer2().getName(), CC.SECONDARY, false)
                            .toString())
                    .build();
            add(skull, new CommandTask("spec " + m.getParticipants().get(0).getName()));
        }
    }
}
