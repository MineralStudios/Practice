package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
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
        if (opponent != null) {
            ItemStack lever = new ItemBuilder(Material.LEVER)
                    .name("View Opponent Inventory").build();
            setSlot(53, lever, p -> p.getPlayer().performCommand("viewinventory " + opponent));
        }
        return true;
    }
}
