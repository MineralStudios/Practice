package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;

public class SelectExistingKnockbackMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Select Existing Knockback";

    public SelectExistingKnockbackMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        for (KnockbackProfile knockback : KnockbackProfileList.getKnockbackProfiles()) {

            if (knockback == null)
                continue;

            add(ItemStacks.KNOCKBACK
                    .name(CC.SECONDARY + CC.B + knockback.getName()).lore(CC.ACCENT + "Click to select.").build(),
                    () -> {
                        viewer.getMatchData().setKnockback(knockback);
                        viewer.openMenu(menu);
                    });
        }

        return true;
    }
}
