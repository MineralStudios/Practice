package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectKnockbackMenu extends PracticeMenu {
    private final MechanicsMenu menu;

    @Override
    public void update() {
        setSlot(2, ItemStacks.CHOOSE_EXISTING_KNOCKBACK,
                interaction -> interaction.getProfile().openMenu(new SelectExistingKnockbackMenu(menu)));

        setSlot(6, ItemStacks.CREATE_CUSTOM_KNOCKBACK,
                interaction -> interaction.getProfile().openMenu(new CreateCustomKnockbackMenu(menu)));
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Select Knockback";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
