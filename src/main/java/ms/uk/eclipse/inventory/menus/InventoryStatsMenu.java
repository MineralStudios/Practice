package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.inventory.PracticeMenu;

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
