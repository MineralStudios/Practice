package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;

public class PearlCooldownMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Modify Value";

    public PearlCooldownMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {

        setSlot(2, ItemStacks.SUBTRACT_1, () -> {
            if (viewer.getMatchData().getPearlCooldown() >= 1) {
                viewer.getMatchData().setPearlCooldown(viewer.getMatchData().getPearlCooldown() - 1);
            }
            viewer.openMenu(PearlCooldownMenu.this);
        });

        setSlot(4, ItemStacks.CLICK_TO_APPLY_CHANGES
                .name("Pearl Cooldown: " + viewer.getMatchData().getPearlCooldown())
                .build(), p -> {
                    p.openMenu(menu);
                    return true;
                });

        setSlot(6, ItemStacks.ADD_1, () -> {
            if (viewer.getMatchData().getPearlCooldown() >= 1) {
                viewer.getMatchData().setPearlCooldown(viewer.getMatchData().getPearlCooldown() + 1);
            }
            viewer.openMenu(PearlCooldownMenu.this);
        });

        return true;
    }
}
