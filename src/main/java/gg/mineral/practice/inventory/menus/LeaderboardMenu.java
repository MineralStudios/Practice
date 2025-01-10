package gg.mineral.practice.inventory.menus;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.category.Category;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.queue.QueuetypeMenuEntry;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import lombok.NoArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class LeaderboardMenu extends PracticeMenu {

    protected Queuetype queuetype;
    protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> menuEntries;

    public LeaderboardMenu(Queuetype queuetype) {
        this.queuetype = queuetype;
        this.menuEntries = queuetype == null ? null : getMenuEntries();
    }

    protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> getMenuEntries() {
        return queuetype.getMenuEntries();
    }

    @Override
    public void update() {

        val global = ItemStacks.GLOBAL_ELO.name(CC.SECONDARY + CC.B + "Global").build();
        val globalMeta = global.getItemMeta();

        try {
            globalMeta.setLore(queuetype.getGlobalLeaderboardLore());
        } catch (Exception e) {
            globalMeta.setLore(null);
        }

        global.setItemMeta(globalMeta);

        setSlot(4, global);

        for (val entry : menuEntries.object2IntEntrySet()) {

            val menuEntry = entry.getKey();

            if (shouldSkip(menuEntry))
                continue;

            val itemBuild = new ItemBuilder(menuEntry.getDisplayItem().clone())
                    .name(CC.SECONDARY + CC.B + menuEntry.getDisplayName());

            if (menuEntry instanceof Gametype gametype) {
                itemBuild.lore(gametype.getLeaderboardLore().toArray(new String[0]));
                val item = itemBuild.build();
                setSlot(entry.getIntValue() + 18, item);
                continue;
            }

            if (menuEntry instanceof Category category) {
                val sb = new GlueList<String>();
                sb.add(CC.SECONDARY + "Includes:");

                for (val g : category.getGametypes())
                    sb.add(CC.WHITE + g.getDisplayName());

                sb.add(" ");
                sb.add(CC.BOARD_SEPARATOR);
                sb.add(CC.ACCENT + "Click to view category.");

                itemBuild.lore(sb.toArray(new String[0]));
                val item = itemBuild.build();

                setSlot(entry.getIntValue() + 18, item, interaction -> interaction.getProfile()
                        .openMenu(new CategorizedLeaderboardMenu(queuetype, category)));
            }
        }

    }

    protected boolean shouldSkip(QueuetypeMenuEntry menuEntry) {
        return menuEntry instanceof Gametype gametype && gametype.isInCategory();
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Leaderboards";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
