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
        if (viewer.isInKitCreator()) {
            setSlot(4, ItemStacks.SAVE_KIT,
                    () -> viewer.getKitCreator().save());
            return true;
        }

        for (int i = 0; i <= 8; i++) {
            final Integer slot = Integer.valueOf(i);
            setSlot(slot, ItemStacks.SAVE_KIT,
                    () -> viewer.getKitEditor().save(slot));
        }

        for (int i = 9; i <= 17; i++) {
            final Integer slot = Integer.valueOf(i);
            setSlot(slot, ItemStacks.DELETE_KIT,
                    () -> viewer.getKitEditor().delete(slot));
        }

        return true;
    }
}
