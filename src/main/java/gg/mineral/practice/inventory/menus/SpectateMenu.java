package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

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
            ItemStack item = queueEntry == null ? ItemStacks.WOOD_AXE
                    : m.getData().getQueueEntry().getGametype().getDisplayItem().clone();

            if (m.getProfile1() == null || m.getProfile2() == null)
                continue;

            ItemStack skull = new ItemBuilder(item.clone())
                    .name(CC.SECONDARY + CC.B + m.getProfile1().getName() + " vs " + m.getProfile2().getName())
                    .lore(
                            CC.WHITE + "Game type:",
                            CC.GOLD + (queueEntry == null ? "Custom"
                                    : m.getData().getQueueEntry().getGametype().getName()),
                            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to spectate.")
                    .build();
            add(skull, interaction -> {
                Profile p = interaction.getProfile();
                p.getPlayer().performCommand("spec " + m.getParticipants().getFirst().getName());
            });
        }

        return true;
    }
}
