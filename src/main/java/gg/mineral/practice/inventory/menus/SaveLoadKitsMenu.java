package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;

public class SaveLoadKitsMenu extends PracticeMenu {
    final static String TITLE = CC.BLUE + "Save Kits";

    public SaveLoadKitsMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        setSlot(4, ItemStacks.SAVE_KIT,
                viewer.isInKitCreator() ? viewer.getKitCreator()::save : viewer.getKitEditor()::save);
        return true;
    }
}
