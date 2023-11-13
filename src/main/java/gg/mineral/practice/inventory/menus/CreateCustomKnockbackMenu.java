package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.match.CustomKnockback;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class CreateCustomKnockbackMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Create Custom Knockback";
    final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
    }

    public CreateCustomKnockbackMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {

        CustomKnockback kb = viewer.getMatchData().getCustomKnockback() == null ? new CustomKnockback()
                : viewer.getMatchData().getCustomKnockback();
        viewer.getMatchData().setCustomKnockback(kb);

        setSlot(0, ItemStacks.FRICTION.name(CC.SECONDARY + CC.B + "Friction")
                .lore(CC.WHITE + "The amount " + CC.SECONDARY + "movement speed and direction",
                        CC.WHITE + "influences knockback magnitude.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(kb.getFriction()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureKnockbackValueMenu(this, value -> {
                        kb.knockbackFriction = value;
                    }));
                });

        setSlot(1, ItemStacks.HORIZONTAL.name(CC.SECONDARY + CC.B + "Horizontal")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "horizontal" + CC.WHITE + " knockback.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(kb.getHorizontal()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureKnockbackValueMenu(this, value -> {
                        kb.knockbackHorizontal = value;
                    }));
                });

        setSlot(2, ItemStacks.VERTICAL.name(CC.SECONDARY + CC.B + "Vertical")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "vertical" + CC.WHITE + " knockback.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(kb.getVertical()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureKnockbackValueMenu(this, value -> {
                        kb.knockbackVertical = value;
                    }));
                });

        setSlot(4, ItemStacks.APPLY, interaction -> {
            Profile p = interaction.getProfile();
            p.getMatchData().setKnockback(kb);
            p.openMenu(menu);
        });

        setSlot(6, ItemStacks.EXTRA_HORIZONTAL
                .name(CC.SECONDARY + CC.B + "Extra Horizontal")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "horizontal knockback" + CC.WHITE + " added",
                        CC.WHITE + "when sprinting/sprint resetting.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(kb.getExtraHorizontal()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureKnockbackValueMenu(this, value -> {
                        kb.knockbackExtraHorizontal = value;
                    }));
                });

        setSlot(7, ItemStacks.EXTRA_VERTICAL.name(CC.SECONDARY + CC.B + "Extra Vertical")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "vertical knockback" + CC.WHITE + " added",
                        CC.WHITE + "when sprinting/sprint resetting.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(kb.getExtraVertical()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureKnockbackValueMenu(this, value -> {
                        kb.knockbackExtraVertical = value;
                    }));
                });

        setSlot(8, ItemStacks.VERTICAL_LIMIT.name(CC.SECONDARY + CC.B + "Vertical Limit")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "limit" + CC.WHITE + " to vertical knockback.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(kb.getVerticalLimit()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureKnockbackValueMenu(this, value -> {
                        kb.knockbackVerticalLimit = value;
                    }));
                });

        return true;
    }

}
