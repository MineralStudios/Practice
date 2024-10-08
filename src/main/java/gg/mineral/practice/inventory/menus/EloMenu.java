package gg.mineral.practice.inventory.menus;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.ProfileData;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class EloMenu extends PracticeMenu {

    private final ProfileData arg;

    @Override
    public void update() {

        for (Queuetype queuetype : QueuetypeManager.getQueuetypes()) {
            if (!queuetype.isRanked())
                continue;
            setSlot(4,
                    ItemStacks.GLOBAL_ELO.name(
                            CC.SECONDARY + CC.B + arg.getName() + "'s Global Elo")
                            .lore(CC.WHITE + "The " + CC.SECONDARY + "average elo" + CC.WHITE
                                    + " across all game types.",
                                    " ",
                                    CC.WHITE + "Currently:",
                                    CC.GOLD + queuetype.getGlobalElo(arg))

                            .build());

            for (Gametype gametype : queuetype.getGametypes().keySet()) {
                if (gametype.isInCatagory())
                    continue;
                ItemStack item = new ItemBuilder(gametype.getDisplayItem())
                        .name(CC.SECONDARY + CC.B + gametype.getDisplayName())
                        .lore(" ",
                                CC.WHITE + arg.getName() + "'s Elo:",
                                CC.GOLD + gametype.getElo(arg))
                        .build();
                setSlot(queuetype.getGametypes().getInt(gametype) + 18, item);
            }

            for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Catagory> entry : queuetype.getCatagories()
                    .object2IntEntrySet()) {
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

                setSlot(entry.getIntValue() + 18, item, interaction -> interaction.getProfile()
                        .openMenu(new CatagorizedEloMenu(arg, queuetype, c)));
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
