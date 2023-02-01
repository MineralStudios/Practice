package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;

public class SelectKnockbackMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Select Knockback";

    public SelectKnockbackMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        setSlot(2, ItemStacks.CHOOSE_EXISTING_KNOCKBACK, p -> {
            p.openMenu(new SelectExistingKnockbackMenu(menu));
            return true;
        });

        setSlot(6, ItemStacks.CREATE_CUSTOM_KNOCKBACK, p -> {
            p.openMenu(new CreateCustomKnockbackMenu(menu));
            return true;
        });

        return true;
    }
}
