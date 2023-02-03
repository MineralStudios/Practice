package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
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
            setSlot(53, ItemStacks.VIEW_OPPONENT_INVENTORY,
                    interaction -> {
                        Profile p = interaction.getProfile();
                        p.getPlayer().performCommand("viewinventory " + opponent);
                    });
        }

        return true;
    }
}
