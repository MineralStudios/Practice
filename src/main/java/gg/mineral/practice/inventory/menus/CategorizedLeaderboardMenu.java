package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.category.Category;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.queue.QueuetypeMenuEntry;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;

@ClickCancelled(true)
public class CategorizedLeaderboardMenu extends LeaderboardMenu {
    private final Category category;

    public CategorizedLeaderboardMenu(Queuetype queuetype, Category category) {
        this.queuetype = queuetype;
        this.category = category;
        this.menuEntries = getMenuEntries();
    }

    @Override
    protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> getMenuEntries() {
        Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> menuEntries = new Object2IntLinkedOpenHashMap<>();
        category.getGametypes().forEach(gametype -> {
            if (gametype.isInCategory())
                menuEntries.put(gametype, queuetype.getMenuEntries().getInt(gametype));
        });
        return menuEntries;
    }

    @Override
    protected boolean shouldSkip(QueuetypeMenuEntry menuEntry) {
        return menuEntry instanceof Gametype gametype && !gametype.isInCategory();
    }

    @Override
    public String getTitle() {
        return CC.BLUE + category.getDisplayName();
    }
}
