package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.match.CustomKnockback;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class CreateCustomKnockbackMenu extends PracticeMenu {
        private final MechanicsMenu menu;
        final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

        static {
                DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
        }

        @Override
        public void update() {

                CustomKnockback kb = viewer.getDuelSettings().getKnockback() != null
                                && viewer.getDuelSettings().getKnockback() instanceof CustomKnockback customKB
                                                ? customKB
                                                : new CustomKnockback();
                viewer.getDuelSettings().setKnockback(kb);

                setSlot(0, ItemStacks.FRICTION.name(CC.SECONDARY + CC.B + "Friction")
                                .lore(CC.WHITE + "The amount " + CC.SECONDARY + "movement speed and direction",
                                                CC.WHITE + "influences knockback magnitude.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(kb.getFriction()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> kb.setFriction(value), double.class)));

                setSlot(1, ItemStacks.HORIZONTAL.name(CC.SECONDARY + CC.B + "Horizontal")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "horizontal" + CC.WHITE + " knockback.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(kb.getHorizontal()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> kb.setHorizontal(value),
                                                                double.class)));

                setSlot(2, ItemStacks.VERTICAL.name(CC.SECONDARY + CC.B + "Vertical")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "vertical" + CC.WHITE + " knockback.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(kb.getVertical()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> kb.setVertical(value), double.class)));

                setSlot(4, ItemStacks.APPLY, interaction -> {
                        Profile p = interaction.getProfile();
                        p.getDuelSettings().setKnockback(kb);
                        p.openMenu(menu);
                });

                setSlot(6, ItemStacks.EXTRA_HORIZONTAL
                                .name(CC.SECONDARY + CC.B + "Extra Horizontal")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "horizontal knockback" + CC.WHITE + " added",
                                                CC.WHITE + "when sprinting/sprint resetting.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(kb.getHorizontalExtra()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> kb.setHorizontalExtra(value),
                                                                double.class)));

                setSlot(7, ItemStacks.EXTRA_VERTICAL.name(CC.SECONDARY + CC.B + "Extra Vertical")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "vertical knockback" + CC.WHITE + " added",
                                                CC.WHITE + "when sprinting/sprint resetting.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(kb.getVerticalExtra()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> kb.setVerticalExtra(value),
                                                                double.class)));

                setSlot(8, ItemStacks.VERTICAL_LIMIT.name(CC.SECONDARY + CC.B + "Vertical Limit")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "limit" + CC.WHITE + " to vertical knockback.",
                                                " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(kb.getVerticalLimit()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> kb.setVerticalLimit(value),
                                                                double.class)));
        }

        @Override
        public String getTitle() {
                return CC.BLUE + "Create Custom Knockback";
        }

        @Override
        public boolean shouldUpdate() {
                return true;
        }

}
