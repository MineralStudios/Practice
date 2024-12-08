package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Menu;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.match.CustomKnockback;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class CreateCustomKnockbackMenu extends PracticeMenu {
    private final Menu menu;
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");
    private final KnockbackProfile[] profiles = KnockbackProfileList.getProfiles().values()
            .toArray(new KnockbackProfile[0]);
    private int kbIndex = 0;
    private boolean oldCombat;
    private CustomKnockback customKnockback;

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
    }

    public void setFriction(double friction) {
        kbIndex = -1;
        customKnockback.setFriction(friction);
    }

    public void setHorizontal(double horizontal) {
        kbIndex = -1;
        customKnockback.setHorizontal(horizontal);
    }

    public void setVertical(double vertical) {
        kbIndex = -1;
        customKnockback.setVertical(vertical);
    }

    public void setHorizontalExtra(double horizontalExtra) {
        kbIndex = -1;
        customKnockback.setHorizontalExtra(horizontalExtra);
    }

    public void setVerticalExtra(double verticalExtra) {
        kbIndex = -1;
        customKnockback.setVerticalExtra(verticalExtra);
    }

    public void setVerticalLimit(double verticalLimit) {
        kbIndex = -1;
        customKnockback.setVerticalLimit(verticalLimit);
    }

    public double getFriction(KnockbackProfile kb) {
        if (kbIndex > 0)
            return (double) kb.getConfigValues().getOrDefault("friction", customKnockback.getFriction());
        return customKnockback.getFriction();
    }

    public double getHorizontal(KnockbackProfile kb) {
        if (kbIndex > 0)
            return (double) kb.getConfigValues().getOrDefault("horizontal", customKnockback.getHorizontal());
        return customKnockback.getHorizontal();
    }

    public double getVertical(KnockbackProfile kb) {
        if (kbIndex > 0)
            return (double) kb.getConfigValues().getOrDefault("vertical", customKnockback.getVertical());
        return customKnockback.getVertical();
    }

    public double getHorizontalExtra(KnockbackProfile kb) {
        if (kbIndex > 0)
            return (double) kb.getConfigValues().getOrDefault("horizontalExtra", customKnockback.getHorizontalExtra());
        return customKnockback.getHorizontalExtra();
    }

    public double getVerticalExtra(KnockbackProfile kb) {
        if (kbIndex > 0)
            return (double) kb.getConfigValues().getOrDefault("verticalExtra", customKnockback.getVerticalExtra());
        return customKnockback.getVerticalExtra();
    }

    public double getVerticalLimit(KnockbackProfile kb) {
        if (kbIndex > 0)
            return (double) kb.getConfigValues().getOrDefault("verticalLimit", customKnockback.getVerticalLimit());
        return customKnockback.getVerticalLimit();
    }

    @Override
    public void update() {

        val duelSettings = viewer.getDuelSettings();

        val kb = kbIndex >= 0 && kbIndex < profiles.length ? profiles[kbIndex] : null;

        if (customKnockback == null)
            customKnockback = duelSettings.getKnockback() != null
                    && duelSettings.getKnockback() instanceof CustomKnockback customKB
                            ? customKB
                            : new CustomKnockback();

        setSlot(4,
                ItemStacks.CHOOSE_EXISTING_KNOCKBACK.lore(
                        CC.WHITE + "Choose a " + CC.SECONDARY + "preconfigured knockback"
                                + CC.WHITE + ".",
                        " ",
                        CC.WHITE + "Currently: " + CC.GOLD + (kb == null ? "Custom"
                                : kb.getName()),
                        " ",
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select knockback.").build(),
                interaction -> {
                    kbIndex++;
                    if (kbIndex >= profiles.length || kbIndex < 0)
                        kbIndex = 0;
                    reload();
                });

        setSlot(19, ItemStacks.FRICTION.name(CC.SECONDARY + CC.B + "Friction")
                .lore(CC.WHITE + "The amount " + CC.SECONDARY + "movement speed and direction",
                        CC.WHITE + "influences knockback magnitude.", " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(getFriction(customKnockback)),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                value -> setFriction(value), double.class)));

        setSlot(20, ItemStacks.HORIZONTAL.name(CC.SECONDARY + CC.B + "Horizontal")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "horizontal" + CC.WHITE + " knockback.", " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(getHorizontal(customKnockback)),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                value -> setHorizontal(value),
                                double.class)));

        setSlot(21, ItemStacks.VERTICAL.name(CC.SECONDARY + CC.B + "Vertical")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "vertical" + CC.WHITE + " knockback.", " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(getVertical(customKnockback)),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                value -> setVertical(value), double.class)));

        setSlot(22, ItemStacks.EXTRA_HORIZONTAL
                .name(CC.SECONDARY + CC.B + "Extra Horizontal")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "horizontal knockback" + CC.WHITE + " added",
                        CC.WHITE + "when sprinting/sprint resetting.", " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(getHorizontalExtra(customKnockback)),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                value -> setHorizontalExtra(value),
                                double.class)));

        setSlot(23, ItemStacks.EXTRA_VERTICAL.name(CC.SECONDARY + CC.B + "Extra Vertical")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "vertical knockback" + CC.WHITE + " added",
                        CC.WHITE + "when sprinting/sprint resetting.", " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(getVerticalExtra(customKnockback)),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                value -> setVerticalExtra(value),
                                double.class)));

        setSlot(24, ItemStacks.VERTICAL_LIMIT.name(CC.SECONDARY + CC.B + "Vertical Limit")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "limit" + CC.WHITE + " to vertical knockback.",
                        " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(getVerticalLimit(customKnockback)),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                value -> setVerticalLimit(value),
                                double.class)));

        setSlot(25, ItemStacks.OLD_COMBAT.lore(
                CC.WHITE + "Play using " + CC.SECONDARY + "old combat" + CC.WHITE
                        + " seen on servers from 2015-2017.",
                " ",
                CC.WHITE + "Currently:", this.oldCombat ? CC.GREEN + "Enabled" : CC.RED + "Disabled", " ",
                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle old combat.").build(),
                interaction -> {
                    this.oldCombat = !this.oldCombat;
                    reload();
                });

        setSlot(36, ItemStacks.BACK, interaction -> interaction.getProfile().openMenu(menu));

        setSlot(40, ItemStacks.APPLY, interaction -> {
            val p = interaction.getProfile();
            p.getDuelSettings().setOldCombat(oldCombat);
            p.getDuelSettings().setKnockback(kb == null ? customKnockback : kb);
            p.openMenu(menu);
        });
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
