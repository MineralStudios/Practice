package gg.mineral.practice.inventory.menus;

import java.util.List;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.math.MathUtil;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class InventoryStatsListMenu extends PracticeMenu {
    private final List<InventoryStatsMenu> list;
    private final String opponent;

    @Override
    public void update() {
        for (val inventoryStatsMenu : list) {
            inventoryStatsMenu.setPreviousMenu(this);
            add(ItemStacks.INVENTORY_STATS.name(CC.SECONDARY + CC.B + inventoryStatsMenu.getTitle())
                    .lore(CC.ACCENT + "Click to view.").build(),
                    interaction -> interaction.getProfile().openMenu(inventoryStatsMenu));
        }

        int size = list.size();
        int invSize = Math.max(MathUtil.roundUp(size, 9), 9);
        int lastSlot = invSize - 1;

        if (opponent != null)
            setSlot(lastSlot, ItemStacks.VIEW_OPPONENT_INVENTORY,
                    interaction -> interaction.getProfile().getPlayer()
                            .performCommand("viewinventory " + opponent));
    }

    @Override
    public String getTitle() {
        if (list.isEmpty())
            return CC.RED + "No Inventories";
        else
            return CC.BLUE + list.get(0).getTitle() + "'s Team";
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }
}
