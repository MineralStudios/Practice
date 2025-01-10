package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.category.Category;
import gg.mineral.practice.entity.ProfileData;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.queue.QueuetypeMenuEntry;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;

@ClickCancelled(true)
public class CategorizedEloMenu extends EloMenu {
    private final Category category;

    public CategorizedEloMenu(ProfileData arg, Queuetype queuetype, Category category) {
        super();
        this.category = category;
        this.arg = arg;
        this.queuetype = queuetype;
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

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
