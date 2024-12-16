package gg.mineral.practice.inventory.menus;

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

import java.math.RoundingMode;
import java.text.DecimalFormat;

@ClickCancelled(true)
@RequiredArgsConstructor
public class CreateCustomKnockbackMenu extends PracticeMenu {
    private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
    }

    private final Menu menu, prevMenu;
    private final KnockbackProfile[] profiles = KnockbackProfileList.getProfiles().values()
            .stream()
            .sorted((profile1, profile2) -> {
                if ("default_kb".equals(profile1.getName())) return -1;
                if ("default_kb".equals(profile2.getName())) return 1;
                return 0;
            })
            .toArray(KnockbackProfile[]::new);
    private int kbIndex = 0;
    private boolean oldCombat;
    private CustomKnockback customKnockback;

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

        for (int i = 0; i <= profiles.length; i++) {
            if (kbIndex >= 0 && profiles[kbIndex].getName().contains("combo_kb")) {
                kbIndex++;
                if (kbIndex >= profiles.length || kbIndex < 0)
                    kbIndex = 0;
                continue;
            }
            break;
        }

        val kb = kbIndex >= 0 && kbIndex < profiles.length ? profiles[kbIndex] : null;

        if (customKnockback == null) {
            customKnockback = duelSettings.getKnockback() != null
                    && duelSettings.getKnockback() instanceof CustomKnockback customKB
                    ? new CustomKnockback(customKB)
                    : new CustomKnockback();

            if (kbIndex >= 0 && kbIndex < profiles.length) {
                val friction = profiles[kbIndex].getConfigValues().getOrDefault("friction", customKnockback.getFriction());
                val horizontal = profiles[kbIndex].getConfigValues().getOrDefault("horizontal", customKnockback.getHorizontal());
                val vertical = profiles[kbIndex].getConfigValues().getOrDefault("vertical", customKnockback.getVertical());
                val horizontalExtra = profiles[kbIndex].getConfigValues().getOrDefault("horizontalExtra", customKnockback.getHorizontalExtra());
                val verticalExtra = profiles[kbIndex].getConfigValues().getOrDefault("verticalExtra", customKnockback.getVerticalExtra());
                val verticalLimit = profiles[kbIndex].getConfigValues().getOrDefault("verticalLimit", customKnockback.getVerticalLimit());
                val oldCombat = profiles[kbIndex].getConfigValues().getOrDefault("oldCombat", duelSettings.isOldCombat());
                customKnockback.setFriction((double) friction);
                customKnockback.setHorizontal((double) horizontal);
                customKnockback.setVertical((double) vertical);
                customKnockback.setHorizontalExtra((double) horizontalExtra);
                customKnockback.setVerticalExtra((double) verticalExtra);
                customKnockback.setVerticalLimit((double) verticalLimit);
                this.oldCombat = (boolean) oldCombat;
            }
        }

        setSlot(10, ItemStacks.FRICTION.name(CC.SECONDARY + CC.B + "Friction")
                        .lore(CC.WHITE + "The amount " + CC.SECONDARY + "movement speed and direction",
                                CC.WHITE + "influences knockback magnitude.", " ",
                                CC.WHITE + "Currently:",
                                CC.GOLD + DECIMAL_FORMAT.format(getFriction(customKnockback)),
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                this::setFriction, double.class)));

        setSlot(11, ItemStacks.HORIZONTAL.name(CC.SECONDARY + CC.B + "Horizontal")
                        .lore(CC.WHITE + "The " + CC.SECONDARY + "horizontal" + CC.WHITE + " knockback.", " ",
                                CC.WHITE + "Currently:",
                                CC.GOLD + DECIMAL_FORMAT.format(getHorizontal(customKnockback)),
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                this::setHorizontal,
                                double.class)));

        setSlot(12, ItemStacks.VERTICAL.name(CC.SECONDARY + CC.B + "Vertical")
                        .lore(CC.WHITE + "The " + CC.SECONDARY + "vertical" + CC.WHITE + " knockback.", " ",
                                CC.WHITE + "Currently:",
                                CC.GOLD + DECIMAL_FORMAT.format(getVertical(customKnockback)),
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                this::setVertical, double.class)));

        setSlot(13, ItemStacks.EXTRA_HORIZONTAL
                        .name(CC.SECONDARY + CC.B + "Extra Horizontal")
                        .lore(CC.WHITE + "The " + CC.SECONDARY + "horizontal knockback" + CC.WHITE + " added",
                                CC.WHITE + "when sprinting/sprint resetting.", " ",
                                CC.WHITE + "Currently:",
                                CC.GOLD + DECIMAL_FORMAT.format(getHorizontalExtra(customKnockback)),
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                this::setHorizontalExtra,
                                double.class)));

        setSlot(14, ItemStacks.EXTRA_VERTICAL.name(CC.SECONDARY + CC.B + "Extra Vertical")
                        .lore(CC.WHITE + "The " + CC.SECONDARY + "vertical knockback" + CC.WHITE + " added",
                                CC.WHITE + "when sprinting/sprint resetting.", " ",
                                CC.WHITE + "Currently:",
                                CC.GOLD + DECIMAL_FORMAT.format(getVerticalExtra(customKnockback)),
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                this::setVerticalExtra,
                                double.class)));

        setSlot(15, ItemStacks.VERTICAL_LIMIT.name(CC.SECONDARY + CC.B + "Vertical Limit")
                        .lore(CC.WHITE + "The " + CC.SECONDARY + "limit" + CC.WHITE + " to vertical knockback.",
                                " ",
                                CC.WHITE + "Currently:",
                                CC.GOLD + DECIMAL_FORMAT.format(getVerticalLimit(customKnockback)),
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> interaction.getProfile()
                        .openMenu(ConfigureValueMenu.of(this,
                                this::setVerticalLimit,
                                double.class)));

        setSlot(16, ItemStacks.OLD_COMBAT.name(CC.SECONDARY + CC.B + "Delayed Combat").lore(
                        CC.WHITE + "Play using " + CC.SECONDARY + "delayed combat" + CC.WHITE
                                + " seen on servers from 2015-2017.",
                        " ",
                        CC.WHITE + "Currently:", this.oldCombat ? CC.GREEN + "Enabled" : CC.RED + "Disabled", " ",
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle delayed combat.").build(),
                interaction -> {
                    this.oldCombat = !this.oldCombat;
                    reload();
                });

        setSlot(29, ItemStacks.BACK, interaction -> interaction.getProfile().openMenu(prevMenu));

        setSlot(31, ItemStacks.APPLY, interaction -> {
            val p = interaction.getProfile();
            p.getDuelSettings().setOldCombat(oldCombat);
            p.getDuelSettings().setKnockback(kb == null ? customKnockback : kb);
            p.openMenu(menu);
        });

        setSlot(33,
                ItemStacks.CHOOSE_EXISTING_KNOCKBACK.lore(
                        CC.WHITE + "Choose a " + CC.SECONDARY + "preconfigured knockback"
                                + CC.WHITE + ".",
                        " ",
                        CC.WHITE + "Currently: " + CC.GOLD + (kb == null ? "Custom"
                                : kb.getName()),
                        " ",
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select knockback.").build(),
                interaction -> {

                    int lastKbIndex = this.kbIndex;

                    for (int i = 0; i <= profiles.length; i++) {
                        kbIndex++;

                        if (kbIndex >= profiles.length || kbIndex < 0)
                            kbIndex = 0;

                        if (profiles[kbIndex].getName().contains("combo_kb"))
                            continue;
                        break;
                    }

                    if (lastKbIndex != kbIndex && kbIndex >= 0 && kbIndex < profiles.length) {
                        double friction = (double) profiles[kbIndex].getConfigValues().getOrDefault("friction", customKnockback.getFriction());
                        double horizontal = (double) profiles[kbIndex].getConfigValues().getOrDefault("horizontal", customKnockback.getHorizontal());
                        double vertical = (double) profiles[kbIndex].getConfigValues().getOrDefault("vertical", customKnockback.getVertical());
                        double horizontalExtra = (double) profiles[kbIndex].getConfigValues().getOrDefault("horizontalExtra", customKnockback.getHorizontalExtra());
                        double verticalExtra = (double) profiles[kbIndex].getConfigValues().getOrDefault("verticalExtra", customKnockback.getVerticalExtra());
                        double verticalLimit = (double) profiles[kbIndex].getConfigValues().getOrDefault("verticalLimit", customKnockback.getVerticalLimit());
                        boolean oldCombat = (boolean) profiles[kbIndex].getConfigValues().getOrDefault("oldCombat", duelSettings.isOldCombat());
                        customKnockback.setFriction(friction);
                        customKnockback.setHorizontal(horizontal);
                        customKnockback.setVertical(vertical);
                        customKnockback.setHorizontalExtra(horizontalExtra);
                        customKnockback.setVerticalExtra(verticalExtra);
                        customKnockback.setVerticalLimit(verticalLimit);
                        this.oldCombat = oldCombat;
                    }

                    reload();
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
