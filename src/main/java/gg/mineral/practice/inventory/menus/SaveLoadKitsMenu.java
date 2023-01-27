package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

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
                    () -> {
                        viewer.getKitEditor().save(slot);
                        reload();
                    });
        }

        Int2ObjectOpenHashMap<ItemStack[]> loadouts = viewer.getKitEditor().getQueueEntry().getCustomKits(viewer);

        if (loadouts == null) {
            return true;
        }

        for (int i = 9; i <= 17; i++) {
            final Integer slot = Integer.valueOf(i - 9);

            if (loadouts.get((int) (slot)) == null) {
                continue;
            }

            setSlot(i, ItemStacks.DELETE_KIT,
                    () -> {
                        viewer.getKitEditor().delete(slot);
                        reload();
                    });
        }

        return true;
    }
}
