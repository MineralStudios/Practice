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

        setSlot(2, ItemStacks.SUBTRACT_1.name(CC.SECONDARY + CC.B + "Subtract")
                .lore(CC.SECONDARY + "Subtracts" + CC.WHITE + " one from the delay.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + viewer.getMatchData().getNoDamageTicks(),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to subtract.")
                .build(),
                () -> {
                    if (viewer.getMatchData().getNoDamageTicks() >= 1) {
                        viewer.getMatchData()
                                .setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() - 1);
                    }

                    viewer.openMenu(HitDelayMenu.this);
                });

        setSlot(4, ItemStacks.APPLY, interaction -> {
            Profile p = interaction.getProfile();
            p.openMenu(menu);
        });

        setSlot(6, ItemStacks.ADD_1.name(CC.SECONDARY + CC.B + "Add")
                .lore(CC.SECONDARY + "Adds" + CC.WHITE + " one to the delay.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + viewer.getMatchData().getNoDamageTicks(),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to add.")
                .build(), () -> {
                    viewer.getMatchData().setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() + 1);
                    viewer.openMenu(HitDelayMenu.this);
                });

        return true;
    }
}
