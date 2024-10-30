package gg.mineral.practice.inventory.menus;

import java.util.List;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class InventoryStatsListMenu extends PracticeMenu {
    private final List<InventoryStatsMenu> list;

    @Override
    public void update() {
        for (val inventoryStatsMenu : list)
            add(ItemStacks.INVENTORY_STATS.name(CC.SECONDARY + CC.B + inventoryStatsMenu.getTitle())
                    .lore(CC.ACCENT + "Click to view.").build(), interaction -> {
                        val p = interaction.getProfile();
                        p.openMenu(inventoryStatsMenu);
                    });
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Inventories";
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }
}
