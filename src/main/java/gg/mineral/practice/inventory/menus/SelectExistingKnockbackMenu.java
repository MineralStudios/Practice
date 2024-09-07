package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class SelectExistingKnockbackMenu extends PracticeMenu {
    private final MechanicsMenu menu;

    @Override
    public void update() {
        for (KnockbackProfile knockback : KnockbackProfileList.getProfiles()) {

            if (knockback == null)
                continue;

            add(ItemStacks.KNOCKBACK
                    .name(CC.SECONDARY + CC.B + knockback.getName()).lore(CC.ACCENT + "Click to select.").build(),
                    () -> {
                        viewer.getMatchData().setKnockback(knockback);
                        viewer.openMenu(menu);
                    });
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Select Existing Knockback";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
