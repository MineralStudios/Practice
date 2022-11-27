package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.core.tasks.CommandTask;
import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;

public class InventoryStatsMenu extends PracticeMenu {
    String opponent;

    public InventoryStatsMenu(InventoryStatsMenu m) {
        super(m);
        this.opponent = m.getOpponent();
    }

    private String getOpponent() {
        return opponent;
    }

    public InventoryStatsMenu(Profile p, String opponent) {
        super(CC.BLUE + p.getName());
        this.opponent = opponent;
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        ItemStack lever = new ItemBuilder(Material.LEVER)
                .name("View Opponent Inventory").build();
        setSlot(53, lever, new CommandTask("viewinventory " + opponent));
        return true;
    }
}
