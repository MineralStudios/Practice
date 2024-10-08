package gg.mineral.practice.inventory.menus;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectExistingKitMenu extends PracticeMenu {

    protected final PracticeMenu menu;
    protected final boolean simple;

    @Override
    public void update() {
        clear();

        for (Gametype g : GametypeManager.getGametypes()) {
            if (g.isInCatagory())
                continue;
            ItemStack item = new ItemBuilder(g.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + g.getDisplayName()).lore(CC.ACCENT + "Click to select.").build();

            add(item, () -> {
                if (simple)
                    viewer.getDuelSettings().setGametype(g);
                else
                    viewer.getDuelSettings().setKit(g.getKit());

                viewer.openMenu(menu);
            });
        }

        for (Catagory c : CatagoryManager.getCatagories()) {
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
            add(item, interaction -> interaction.getProfile()
                    .openMenu(new SelectCategorizedExistingKitMenu(c, menu, simple)));
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Select Existing Kit";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
