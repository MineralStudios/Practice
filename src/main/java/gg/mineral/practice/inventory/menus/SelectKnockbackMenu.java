package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

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
        setSlot(2, ItemStacks.CHOOSE_EXISTING_KNOCKBACK, interaction -> {
            Profile p = interaction.getProfile();
            p.openMenu(new SelectExistingKnockbackMenu(menu));
        });

        setSlot(6, ItemStacks.CREATE_CUSTOM_KNOCKBACK, interaction -> {
            Profile p = interaction.getProfile();
            p.openMenu(new CreateCustomKnockbackMenu(menu));
        });

        return true;
    }
}
