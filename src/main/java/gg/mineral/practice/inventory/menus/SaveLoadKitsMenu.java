package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;

public class SaveLoadKitsMenu extends PracticeMenu {
    final static String TITLE = CC.BLUE + "Save/Load Kits";

    public SaveLoadKitsMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        ItemStack item = new ItemBuilder(new ItemStack(160, 1, (short) 13))
                .name("Save Kit").build();
        Runnable r = viewer.isInKitCreator() ? viewer.getKitCreator()::save : viewer.getKitEditor()::save;
        setSlot(4, item, r);
        return true;
    }
}
