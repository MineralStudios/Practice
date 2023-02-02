package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import gg.mineral.api.knockback.KnockbackMode;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfile;

public class CreateCustomKnockbackMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Create Custom Knockback";
    final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.CEILING);
    }

    public CreateCustomKnockbackMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {

        KnockbackProfile kb = viewer.getMatchData().getCustomKnockback() == null ? new KnockbackProfile("Custom")
                : viewer.getMatchData().getCustomKnockback();
        kb.setKnockbackMode(KnockbackMode.NORMAL);
        viewer.getMatchData().setCustomKnockback(kb);

        setSlot(0, ItemStacks.ADD_0_01, () -> {
            kb.knockbackFriction += 0.01;
            reload();
        });

        setSlot(1, ItemStacks.ADD_0_01, () -> {
            kb.knockbackHorizontal += 0.01;
            reload();
        });

        setSlot(2, ItemStacks.ADD_0_01, () -> {
            kb.knockbackExtraHorizontal += 0.01;
            reload();
        });

        setSlot(3, ItemStacks.ADD_0_01, () -> {
            kb.knockbackVertical += 0.01;
            reload();
        });

        setSlot(4, ItemStacks.ADD_0_01, () -> {
            kb.knockbackExtraVertical += 0.01;
            reload();
        });

        setSlot(5, ItemStacks.ADD_0_01, () -> {
            kb.knockbackVerticalLimit += 0.01;
            reload();
        });

        setSlot(9, ItemStacks.ADD_0_001, () -> {
            kb.knockbackFriction += 0.001;
            reload();
        });

        setSlot(10, ItemStacks.ADD_0_001, () -> {
            kb.knockbackHorizontal += 0.001;
            reload();
        });

        setSlot(11, ItemStacks.ADD_0_001, () -> {
            kb.knockbackExtraHorizontal += 0.001;
            reload();
        });

        setSlot(12, ItemStacks.ADD_0_001, () -> {
            kb.knockbackVertical += 0.001;
            reload();
        });

        setSlot(13, ItemStacks.ADD_0_001, () -> {
            kb.knockbackExtraVertical += 0.001;
            reload();
        });

        setSlot(14, ItemStacks.ADD_0_001, () -> {
            kb.knockbackVerticalLimit += 0.001;
            reload();
        });

        setSlot(18, ItemStacks.FRICTION.name("Friction: " + DECIMAL_FORMAT.format(kb.getFriction())).build());

        setSlot(19, ItemStacks.HORIZONTAL.name("Horizontal: " + DECIMAL_FORMAT.format(kb.getHorizontal())).build());

        setSlot(20, ItemStacks.EXTRA_HORIZONTAL
                .name("Extra Horizontal: " + DECIMAL_FORMAT.format(kb.getExtraHorizontal())).build());

        setSlot(21, ItemStacks.VERTICAL.name("Vertical: " + DECIMAL_FORMAT.format(kb.getVertical())).build());

        setSlot(22, ItemStacks.EXTRA_VERTICAL.name("Extra Vertical: " + DECIMAL_FORMAT.format(kb.getExtraVertical()))
                .build());

        setSlot(23, ItemStacks.VERTICAL_LIMIT.name("Vertical Limit: " + DECIMAL_FORMAT.format(kb.getVerticalLimit()))
                .build());

        setSlot(27, ItemStacks.SUBTRACT_0_001, () -> {
            kb.knockbackFriction -= 0.001;
            reload();
        });

        setSlot(28, ItemStacks.SUBTRACT_0_001, () -> {
            kb.knockbackHorizontal -= 0.001;
            reload();
        });

        setSlot(29, ItemStacks.SUBTRACT_0_001, () -> {
            kb.knockbackExtraHorizontal -= 0.001;
            reload();
        });

        setSlot(30, ItemStacks.SUBTRACT_0_001, () -> {
            kb.knockbackVertical -= 0.001;
            reload();
        });

        setSlot(31, ItemStacks.SUBTRACT_0_001, () -> {
            kb.knockbackExtraVertical -= 0.001;
            reload();
        });

        setSlot(32, ItemStacks.SUBTRACT_0_001, () -> {
            kb.knockbackVerticalLimit -= 0.001;
            reload();
        });

        setSlot(36, ItemStacks.SUBTRACT_0_01, () -> {
            kb.knockbackFriction -= 0.01;
            reload();
        });

        setSlot(37, ItemStacks.SUBTRACT_0_01, () -> {
            kb.knockbackHorizontal -= 0.01;
            reload();
        });

        setSlot(38, ItemStacks.SUBTRACT_0_01, () -> {
            kb.knockbackExtraHorizontal -= 0.01;
            reload();
        });

        setSlot(39, ItemStacks.SUBTRACT_0_01, () -> {
            kb.knockbackVertical -= 0.01;
            reload();
        });

        setSlot(40, ItemStacks.SUBTRACT_0_01, () -> {
            kb.knockbackExtraVertical -= 0.01;
            reload();
        });

        setSlot(41, ItemStacks.SUBTRACT_0_01, () -> {
            kb.knockbackVerticalLimit -= 0.01;
            reload();
        });

        setSlot(49, ItemStacks.CLICK_TO_APPLY_CHANGES.name("Custom Knockback").build(), p -> {
            p.getMatchData().setKnockback(kb);
            p.openMenu(menu);
            return true;
        });

        return true;
    }

}
