package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectKitMenu extends PracticeMenu {
    private final MechanicsMenu menu;

    @Override
    public void update() {
        setSlot(11, ItemStacks.CHOOSE_EXISTING_KIT,
                interaction -> interaction.getProfile().openMenu(new SelectExistingKitMenu(menu,menu, false)));

        setSlot(15, ItemStacks.CHOOSE_CUSTOM_KIT, interaction -> {
            val viewer = interaction.getProfile();
            viewer.getPlayer().closeInventory();
            viewer.sendToKitCreator(menu.getSubmitAction());
        });

        if (menu != null)
            setSlot(31, ItemStacks.BACK, interaction -> interaction.getProfile().openMenu(menu));
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
