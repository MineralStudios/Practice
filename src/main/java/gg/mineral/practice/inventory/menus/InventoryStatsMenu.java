package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.Profile;
import gg.mineral.api.inventory.InventoryBuilder;

public class InventoryStatsMenu implements InventoryBuilder {
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
        setItemDragging(true);
    }

    @Override
    public MineralInventory build(Profile profile) {
        ItemStack lever = new ItemBuilder(Material.LEVER)
                .name("View Opponent Inventory").build();
        set(53, lever, new CommandTask("viewinventory " + opponent));
        return true;
    }
}
