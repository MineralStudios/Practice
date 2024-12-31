package gg.mineral.practice.inventory.menus;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.category.Category;
import gg.mineral.practice.entity.ProfileData;
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
public class EloMenu extends PracticeMenu {

    protected ProfileData arg;
    protected Queuetype queuetype;
    protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> menuEntries;

    public EloMenu(ProfileData arg, Queuetype queuetype) {
        this.arg = arg;
        this.queuetype = queuetype;
        this.menuEntries = getMenuEntries();
    }

    protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> getMenuEntries() {
        return queuetype.getMenuEntries();
    }

    protected boolean shouldSkip(QueuetypeMenuEntry menuEntry) {
        return menuEntry instanceof Gametype gametype && gametype.isInCategory();
    }

    @Override
    public void update() {
        setSlot(4,
                ItemStacks.GLOBAL_ELO.name(
                                CC.SECONDARY + CC.B + arg.getName() + "'s Global Elo")
                        .lore(CC.WHITE + "The " + CC.SECONDARY + "average elo" + CC.WHITE
                                        + " across all game types.",
                                " ",
                                CC.WHITE + "Currently:",
                                CC.GOLD + queuetype.getGlobalElo(arg))

                        .build());

        for (val entry : menuEntries.object2IntEntrySet()) {
            val menuEntry = entry.getKey();
            if (shouldSkip(menuEntry))
                continue;

            if (menuEntry instanceof Gametype gametype) {
                val item = new ItemBuilder(menuEntry.getDisplayItem())
                        .name(CC.SECONDARY + CC.B + menuEntry.getDisplayName())
                        .lore(" ",
                                CC.WHITE + arg.getName() + "'s Elo:",
                                CC.GOLD + gametype.getElo(arg))
                        .build();
                setSlot(entry.getIntValue() + 18, item);
                continue;
            }

            if (menuEntry instanceof Category c) {
                val itemBuild = new ItemBuilder(c.getDisplayItem())
                        .name(CC.SECONDARY + CC.B + c.getDisplayName());

                val sb = new GlueList<String>();
                sb.add(CC.SECONDARY + "Includes:");

                for (val g : c.getGametypes())
                    sb.add(CC.WHITE + g.getDisplayName());

                sb.add(" ");
                sb.add(CC.BOARD_SEPARATOR);
                sb.add(CC.ACCENT + "Click to view category.");

                itemBuild.lore(sb.toArray(new String[0]));
                val item = itemBuild.build();

                setSlot(entry.getIntValue() + 18, item, interaction -> interaction.getProfile()
                        .openMenu(new CategorizedEloMenu(arg, queuetype, c)));
            }
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + arg.getName();
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
