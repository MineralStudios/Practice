package gg.mineral.practice.inventory.menus;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.match.OldStyleKnockback;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.inventory.ItemStack;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectExistingKitMenu extends PracticeMenu {

    protected final PracticeMenu menu, prevMenu;
    protected final boolean simple;

    @Override
    public void update() {
        clear();

        for (val g : GametypeManager.getGametypes().values()) {
            if (g.isInCatagory())
                continue;
            val item = new ItemBuilder(g.getDisplayItem())
                    .name(CC.SECONDARY + CC.B + g.getDisplayName()).lore(CC.ACCENT + "Click to select.").build();

            addAfter(9, item, interaction -> {
                if (simple)
                    viewer.getDuelSettings().setGametype(g);
                else
                    viewer.getDuelSettings().setKit(g.getKit());

                viewer.openMenu(menu);
            });
        }

        for (val c : CatagoryManager.getCatagories()) {
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
            ItemStack item = itemBuild.build();
            addAfter(9, item, interaction -> interaction.getProfile()
                    .openMenu(new SelectCategorizedExistingKitMenu(c, menu, simple)));
        }

        if (prevMenu != null)
            setSlot(simple ? 39 : 40, ItemStacks.BACK, interaction -> viewer.openMenu(prevMenu));

        val oldCombat = viewer.getDuelSettings().isOldCombat();

        if (simple)
            setSlot(41, ItemStacks.OLD_COMBAT.name(CC.SECONDARY + CC.B + "Old Combat Mechanics").lore(
                            CC.WHITE + "Play using " + CC.SECONDARY + "old combat" + CC.WHITE
                                    + " seen on servers from 2015-2017.",
                            " ",
                            CC.WHITE + "Currently:", oldCombat ? CC.GREEN + "Enabled" : CC.RED + "Disabled", " ",
                            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle old combat.").build(),
                    interaction -> {
                        interaction.getProfile().getDuelSettings().setOldCombat(!oldCombat);
                        interaction.getProfile().getDuelSettings().setKnockback(new OldStyleKnockback());
                        reload();
                    });
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
