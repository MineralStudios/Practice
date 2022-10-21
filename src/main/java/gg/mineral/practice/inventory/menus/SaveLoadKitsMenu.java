package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.api.inventory.InventoryBuilder;

public class SaveLoadKitsMenu implements InventoryBuilder {
    final static String TITLE = CC.BLUE + "Save/Load Kits";

    public SaveLoadKitsMenu() {
        super(TITLE);
        setItemDragging(true);
    }

    @Override
    public MineralInventory build(Profile profile) {
        ItemStack item = new ItemBuilder(new ItemStack(160, 1, (short) 13))
                .name("Save Kit").build();
        Runnable r = viewer.getPlayerStatus() == PlayerStatus.KIT_CREATOR ? viewer::saveCreatedKit : viewer::saveKit;
        set(4, item, r);
        return true;
    }
}
