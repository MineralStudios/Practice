package gg.mineral.practice.inventory.menus;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class LeaderboardMenu extends PracticeMenu {

    final static String TITLE = CC.BLUE + "Leaderboards";

    public LeaderboardMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        QueuetypeManager.getQueuetypes().stream().filter(q -> q.isRanked())
                .findFirst().ifPresent(queuetype -> {
                    setSlot(4, ItemStacks.LEADERBOARD);

                    for (Entry<Gametype, Integer> entry : queuetype.getGametypes().object2IntEntrySet()) {

                        Gametype gametype = entry.getKey();

                        if (gametype.isInCatagory())
                            continue;

                        ItemStack item = new ItemBuilder(gametype.getDisplayItem())
                                .name(gametype.getDisplayName()).build();
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
                                .name(c.getDisplayName());

                        List<String> sb = new GlueList<String>();
                        sb.add(CC.SECONDARY + "Includes:");

                        for (Gametype g : c.getGametypes()) {
                            sb.add(g.getDisplayName());
                        }

                        itemBuild.lore(sb.toArray(new String[0]));
                        ItemStack item = itemBuild.build();

                        setSlot(entry.getValue() + 18, item, interaction -> {
                            Profile p = interaction.getProfile();
                            p.openMenu(new CatagorizedLeaderboardMenu(queuetype, c));
                        });
                    }

                });

        return true;
    }
}
