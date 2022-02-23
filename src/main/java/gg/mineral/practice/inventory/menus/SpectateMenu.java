package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.core.tasks.CommandTask;
import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.queue.QueueEntry;

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
