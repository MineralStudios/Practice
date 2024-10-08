package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.kit.KitEditor;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

@ClickCancelled(true)
public class SaveLoadKitsMenu extends PracticeMenu {

    @Override
    public void update() {
        if (viewer.isInKitCreator()) {
            setSlot(4, ItemStacks.SAVE_KIT,
                    () -> viewer.getKitCreator().save());
            return;
        }

        KitEditor kitEditor = viewer.getKitEditor();
        Int2ObjectOpenHashMap<ItemStack[]> loadouts = viewer.getCustomKits(kitEditor.getQueuetype(),
                kitEditor.getGametype());

        if (loadouts == null)
            return;

        for (int i = 0; i <= 8; i++) {
            final int slot = i;
            if (!loadouts.containsKey(i))
                setSlot(slot, ItemStacks.SAVE_KIT,
                        () -> {
                            viewer.getKitEditor().save(slot);
                            reload();
                        });
            else
                setSlot(i, ItemStacks.DELETE_KIT,
                        () -> {
                            viewer.getKitEditor().delete(slot);
                            reload();
                        });
        }

        return;
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Save Kits";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
