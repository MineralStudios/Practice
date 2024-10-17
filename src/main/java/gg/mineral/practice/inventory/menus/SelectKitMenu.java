package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectKitMenu extends PracticeMenu {
    private final MechanicsMenu menu;

    @Override
    public void update() {
        setSlot(2, ItemStacks.CHOOSE_EXISTING_KIT,
                interaction -> interaction.getProfile().openMenu(new SelectExistingKitMenu(menu, false)));

        setSlot(6, ItemStacks.CHOOSE_CUSTOM_KIT, interaction -> {
            Profile viewer = interaction.getProfile();
            viewer.getPlayer().closeInventory();
            viewer.sendToKitCreator(menu.getSubmitAction());
        });
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Select Kit";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
