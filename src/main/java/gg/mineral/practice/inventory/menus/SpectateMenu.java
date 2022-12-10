package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.queue.QueueEntry;

public class SpectateMenu extends PracticeMenu {

    final static String TITLE = CC.BLUE + "Spectate";

    public SpectateMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        clear();
        for (Match m : MatchManager.getMatchs()) {
            QueueEntry queueEntry = m.getData().getQueueEntry();
            Material item = queueEntry == null ? Material.WOOD_AXE
                    : m.getData().getQueueEntry().getGametype().getDisplayItem().getType();
            ItemStack skull = new ItemBuilder(item)
                    .name(CC.SECONDARY + m.getPlayer1().getName() + " vs " + m.getPlayer2().getName()).lore()
                    .build();
            add(skull, p -> p.bukkit().performCommand("spec " + m.getParticipants().get(0).getName()));
        }

        return true;
    }
}
