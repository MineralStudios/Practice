package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

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

        setSlot(2, ItemStacks.SUBTRACT_1.name("Subtract 1 (" + viewer.getMatchData().getNoDamageTicks() + ")").build(),
                () -> {
                    if (viewer.getMatchData().getNoDamageTicks() >= 1) {
                        viewer.getMatchData()
                                .setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() - 1);
                    }

                    viewer.openMenu(HitDelayMenu.this);
                });

        setSlot(4, ItemStacks.CLICK_TO_APPLY_CHANGES.name("Hit Delay: " + viewer.getMatchData().getNoDamageTicks())
                .build(), interaction -> {
                    Profile p = interaction.getProfile();
                    p.openMenu(menu);
                });

        setSlot(6, ItemStacks.ADD_1.name("Add 1 (" + viewer.getMatchData().getNoDamageTicks() + ")").build(), () -> {
            viewer.getMatchData().setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() + 1);
            viewer.openMenu(HitDelayMenu.this);
        });

        return true;
    }
}
