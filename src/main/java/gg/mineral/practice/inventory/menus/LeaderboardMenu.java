package gg.mineral.practice.inventory.menus;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

@ClickCancelled(true)
public class LeaderboardMenu extends PracticeMenu {

    @Override
    public void update() {
        Queuetype[] queuetypes = QueuetypeManager.getQueuetypes();

        for (Queuetype queuetype : queuetypes) {
            if (!queuetype.isRanked())
                continue;

            ItemStack global = ItemStacks.GLOBAL_ELO.name(CC.SECONDARY + CC.B + "Global").build();
            ItemMeta globalMeta = global.getItemMeta();

            try {
                globalMeta.setLore(queuetype.getGlobalLeaderboardLore());
            } catch (Exception e) {
                globalMeta.setLore(null);
            }

            global.setItemMeta(globalMeta);

            setSlot(4, global);

            for (Entry<Gametype, Integer> entry : queuetype.getGametypes().object2IntEntrySet()) {

                Gametype gametype = entry.getKey();

                if (gametype.isInCatagory())
                    continue;

                ItemStack item = new ItemBuilder(gametype.getDisplayItem().clone())
                        .name(CC.SECONDARY + CC.B + gametype.getDisplayName()).build();
                ItemMeta meta = item.getItemMeta();

                try {
                    meta.setLore(gametype.getLeaderboardLore());
                } catch (Exception e) {
                    meta.setLore(null);
                }

                item.setItemMeta(meta);
                setSlot(entry.getValue() + 18, item);
            }

            for (Entry<Catagory, Integer> entry : queuetype.getCatagories().object2IntEntrySet()) {
                Catagory c = entry.getKey();
                ItemBuilder itemBuild = new ItemBuilder(c.getDisplayItem())
                        .name(CC.SECONDARY + CC.B + c.getDisplayName());

                List<String> sb = new GlueList<String>();
                sb.add(CC.SECONDARY + "Includes:");

                for (Gametype g : c.getGametypes())
                    sb.add(CC.WHITE + g.getDisplayName());

                sb.add(" ");
                sb.add(CC.BOARD_SEPARATOR);
                sb.add(CC.ACCENT + "Click to view catagory.");

                itemBuild.lore(sb.toArray(new String[0]));
                ItemStack item = itemBuild.build();

                setSlot(entry.getValue() + 18, item, interaction -> interaction.getProfile()
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
