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
        for (Match m : MatchManager.getMatches()) {
            QueueEntry queueEntry = m.getData().getQueueEntry();
            Material item = queueEntry == null ? Material.WOOD_AXE
                    : m.getData().getQueueEntry().getGametype().getDisplayItem().getType();
            ItemStack skull = new ItemBuilder(item)
                    .name(CC.SECONDARY + m.getProfile1().getName() + " vs " + m.getProfile2().getName()).lore()
                    .build();
            add(skull, p -> p.getPlayer().performCommand("spec " + m.getParticipants().get(0).getName()));
        }

        return true;
    }
}
