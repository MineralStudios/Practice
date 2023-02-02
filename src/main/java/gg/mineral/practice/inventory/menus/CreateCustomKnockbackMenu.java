package gg.mineral.practice.inventory.menus;

import gg.mineral.api.knockback.KnockbackMode;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfile;

public class CreateCustomKnockbackMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Create Custom Knockback";

    public CreateCustomKnockbackMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {

        KnockbackProfile kb = viewer.getMatchData().getKnockback() == null ? new KnockbackProfile("Custom")
                : viewer.getMatchData().getKnockback();
        kb.setKnockbackMode(KnockbackMode.NORMAL);
        viewer.getMatchData().setKnockback(kb);

        setSlot(0, ItemStacks.ADD_0_01, () -> {
            kb.setFriction(kb.getFriction() + 0.01);
            reload();
        });

        setSlot(1, ItemStacks.ADD_0_01, () -> {
            kb.setHorizontal(kb.getHorizontal() + 0.01);
            reload();
        });

        setSlot(2, ItemStacks.ADD_0_01, () -> {
            kb.setExtraHorizontal(kb.getExtraHorizontal() + 0.01);
            reload();
        });

        setSlot(3, ItemStacks.ADD_0_01, () -> {
            kb.setVertical(kb.getVertical() + 0.01);
            reload();
        });

        setSlot(4, ItemStacks.ADD_0_01, () -> {
            kb.setExtraVertical(kb.getExtraVertical() + 0.01);
            reload();
        });

        setSlot(5, ItemStacks.ADD_0_01, () -> {
            kb.setVerticalLimit(kb.getVerticalLimit() + 0.01);
            reload();
        });

        setSlot(9, ItemStacks.ADD_0_001, () -> {
            kb.setFriction(kb.getFriction() + 0.001);
            reload();
        });

        setSlot(10, ItemStacks.ADD_0_001, () -> {
            kb.setHorizontal(kb.getHorizontal() + 0.001);
            reload();
        });

        setSlot(11, ItemStacks.ADD_0_001, () -> {
            kb.setExtraHorizontal(kb.getExtraHorizontal() + 0.001);
            reload();
        });

        setSlot(12, ItemStacks.ADD_0_001, () -> {
            kb.setVertical(kb.getVertical() + 0.001);
            reload();
        });

        setSlot(13, ItemStacks.ADD_0_001, () -> {
            kb.setExtraVertical(kb.getExtraVertical() + 0.001);
            reload();
        });

        setSlot(14, ItemStacks.ADD_0_001, () -> {
            kb.setVerticalLimit(kb.getVerticalLimit() + 0.001);
            reload();
        });

        setSlot(18, ItemStacks.FRICTION.name("Friction: " + kb.getFriction()).build());

        setSlot(19, ItemStacks.HORIZONTAL.name("Horizontal: " + kb.getHorizontal()).build());

        setSlot(20, ItemStacks.EXTRA_HORIZONTAL.name("Extra Horizontal: " + kb.getExtraHorizontal()).build());

        setSlot(21, ItemStacks.VERTICAL.name("Vertical: " + kb.getVertical()).build());

        setSlot(22, ItemStacks.EXTRA_VERTICAL.name("Extra Vertical: " + kb.getExtraVertical()).build());

        setSlot(23, ItemStacks.VERTICAL_LIMIT.name("Vertical Limit: " + kb.getVerticalLimit()).build());

        setSlot(27, ItemStacks.SUBTRACT_0_001, () -> {
            kb.setFriction(kb.getFriction() - 0.001);
            reload();
        });

        setSlot(28, ItemStacks.SUBTRACT_0_001, () -> {
            kb.setHorizontal(kb.getHorizontal() - 0.001);
            reload();
        });

        setSlot(29, ItemStacks.SUBTRACT_0_001, () -> {
            kb.setExtraHorizontal(kb.getExtraHorizontal() - 0.001);
            reload();
        });

        setSlot(30, ItemStacks.SUBTRACT_0_001, () -> {
            kb.setVertical(kb.getVertical() - 0.001);
            reload();
        });

        setSlot(31, ItemStacks.SUBTRACT_0_001, () -> {
            kb.setExtraVertical(kb.getExtraVertical() - 0.001);
            reload();
        });

        setSlot(32, ItemStacks.SUBTRACT_0_001, () -> {
            kb.setVerticalLimit(kb.getVerticalLimit() - 0.001);
            reload();
        });

        setSlot(36, ItemStacks.SUBTRACT_0_01, () -> {
            kb.setFriction(kb.getFriction() - 0.01);
            reload();
        });

        setSlot(37, ItemStacks.SUBTRACT_0_01, () -> {
            kb.setHorizontal(kb.getHorizontal() - 0.01);
            reload();
        });

        setSlot(38, ItemStacks.SUBTRACT_0_01, () -> {
            kb.setExtraHorizontal(kb.getExtraHorizontal() - 0.01);
            reload();
        });

        setSlot(39, ItemStacks.SUBTRACT_0_01, () -> {
            kb.setVertical(kb.getVertical() - 0.01);
            reload();
        });

        setSlot(40, ItemStacks.SUBTRACT_0_01, () -> {
            kb.setExtraVertical(kb.getExtraVertical() - 0.01);
            reload();
        });

        setSlot(41, ItemStacks.SUBTRACT_0_01, () -> {
            kb.setVerticalLimit(kb.getVerticalLimit() - 0.01);
            reload();
        });

        setSlot(48, ItemStacks.CLICK_TO_APPLY_CHANGES.name("Custom Knockback").build(), p -> {
            p.openMenu(menu);
            return true;
        });

        return true;
    }
}
