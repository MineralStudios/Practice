package gg.mineral.practice.inventory.menus;

import gg.mineral.api.collection.GlueList;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

import lombok.val;

@ClickCancelled(true)
public class LeaderboardMenu extends PracticeMenu {

    @Override
    public void update() {
        val queuetypes = QueuetypeManager.getQueuetypes().values();

        for (val queuetype : queuetypes) {
            if (!queuetype.isRanked())
                continue;

            val global = ItemStacks.GLOBAL_ELO.name(CC.SECONDARY + CC.B + "Global").build();
            val globalMeta = global.getItemMeta();

            try {
                globalMeta.setLore(queuetype.getGlobalLeaderboardLore());
            } catch (Exception e) {
                globalMeta.setLore(null);
            }

            global.setItemMeta(globalMeta);

            setSlot(4, global);

            for (val entry : queuetype.getGametypes().object2IntEntrySet()) {

                val gametype = entry.getKey();

                if (gametype.isInCatagory())
                    continue;

                val item = new ItemBuilder(gametype.getDisplayItem().clone())
                        .name(CC.SECONDARY + CC.B + gametype.getDisplayName()).build();
                val meta = item.getItemMeta();

                try {
                    meta.setLore(gametype.getLeaderboardLore());
                } catch (Exception e) {
                    meta.setLore(null);
                }

                item.setItemMeta(meta);
                setSlot(entry.getIntValue() + 18, item);
            }

            for (val entry : queuetype.getCatagories().object2IntEntrySet()) {
                val c = entry.getKey();
                val itemBuild = new ItemBuilder(c.getDisplayItem())
                        .name(CC.SECONDARY + CC.B + c.getDisplayName());

                val sb = new GlueList<String>();
                sb.add(CC.SECONDARY + "Includes:");

                for (val g : c.getGametypes())
                    sb.add(CC.WHITE + g.getDisplayName());

                sb.add(" ");
                sb.add(CC.BOARD_SEPARATOR);
                sb.add(CC.ACCENT + "Click to view catagory.");

                itemBuild.lore(sb.toArray(new String[0]));
                val item = itemBuild.build();

                setSlot(entry.getIntValue() + 18, item, interaction -> interaction.getProfile()
                        .openMenu(new CatagorizedLeaderboardMenu(queuetype, c)));
            }
        }

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
