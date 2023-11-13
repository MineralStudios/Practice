package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class SelectKitMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Select Kit";

    public SelectKitMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        setSlot(2, ItemStacks.CHOOSE_EXISTING_KIT, interaction -> {
            Profile p = interaction.getProfile();
            p.openMenu(new SelectExistingKitMenu(menu, false));
        });

        setSlot(6, ItemStacks.CHOOSE_CUSTOM_KIT, () -> {
            viewer.getPlayer().closeInventory();
            viewer.sendToKitCreator(menu.getSubmitAction());
        });

        return true;
    }
}
