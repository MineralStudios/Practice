package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class SelectOpponentTypeMenu extends PracticeMenu {
    QueueEntry queueEntry;
    SelectGametypeMenu menu;
    final static String TITLE = CC.BLUE + "Select Opponent Type";

    public SelectOpponentTypeMenu(QueueEntry queueEntry, SelectGametypeMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.queueEntry = queueEntry;
        this.menu = menu;

    }

    @Override
    public boolean update() {

        setSlot(2, ItemStacks.BOT_MODE, interaction -> {
            viewer.openMenu(new SelectBotDifficultyMenu(queueEntry));
        });

        setSlot(6, ItemStacks.PLAYER_MODE, interaction -> {
            Profile p = interaction.getProfile();
            p.addPlayerToQueue(queueEntry);
            viewer.openMenu(menu);
        });

        return true;
    }
}
