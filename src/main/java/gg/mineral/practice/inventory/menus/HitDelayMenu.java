package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;

public class HitDelayMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Modify Value";

    public HitDelayMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {

        setSlot(2, ItemStacks.SUBTRACT_1, () -> {
            if (viewer.getMatchData().getNoDamageTicks() >= 1) {
                viewer.getMatchData()
                        .setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() - 1);
            }

            viewer.openMenu(HitDelayMenu.this);
        });

        setSlot(4, ItemStacks.CLICK_TO_APPLY_CHANGES.name("Hit Delay: " + viewer.getMatchData().getNoDamageTicks())
                .build(), p -> {
                    p.openMenu(menu);
                    return true;
                });

        setSlot(6, ItemStacks.ADD_1, () -> {
            viewer.getMatchData().setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() + 1);
            viewer.openMenu(HitDelayMenu.this);
        });

        return true;
    }
}
