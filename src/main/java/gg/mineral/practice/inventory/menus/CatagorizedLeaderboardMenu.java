package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.queue.QueuetypeMenuEntry;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;

@ClickCancelled(true)
public class CatagorizedLeaderboardMenu extends LeaderboardMenu {
    private final Catagory catagory;

    public CatagorizedLeaderboardMenu(Queuetype queuetype, Catagory catagory) {
        this.queuetype = queuetype;
        this.catagory = catagory;
        this.menuEntries = getMenuEntries();
    }

    @Override
    protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> getMenuEntries() {
        Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> menuEntries = new Object2IntLinkedOpenHashMap<>();
        catagory.getGametypes().forEach(gametype -> {
            if (gametype.isInCatagory())
                menuEntries.put(gametype, queuetype.getMenuEntries().getInt(gametype));
        });
        return menuEntries;
    }

    @Override
    protected boolean shouldSkip(QueuetypeMenuEntry menuEntry) {
        return menuEntry instanceof Gametype gametype && !gametype.isInCatagory();
    }

    @Override
    public String getTitle() {
        return CC.BLUE + catagory.getDisplayName();
    }
}
