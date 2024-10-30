package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

import lombok.val;

@ClickCancelled(true)
public class SaveLoadKitsMenu extends PracticeMenu {

    @Override
    public void update() {
        if (viewer.isInKitCreator()) {
            setSlot(4, ItemStacks.SAVE_KIT,
                    interaction -> viewer.getKitCreator().save());
            return;
        }

        val kitEditor = viewer.getKitEditor();
        val loadouts = viewer.getCustomKits(kitEditor.getQueuetype(),
                kitEditor.getGametype());

        if (loadouts == null)
            return;

        for (int i = 0; i <= 8; i++) {
            final int slot = i;
            if (!loadouts.containsKey(i))
                setSlot(slot, ItemStacks.SAVE_KIT,
                        interaction -> {
                            viewer.getKitEditor().save(slot);
                            reload();
                        });
            else
                setSlot(i, ItemStacks.DELETE_KIT,
                        interaction -> {
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
