package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.bukkit.event.inventory.ClickType;

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

        String frictionStr = "Friction: " + DECIMAL_FORMAT.format(kb.getFriction());
        String horizontalStr = "Horizontal: " + DECIMAL_FORMAT.format(kb.getHorizontal());
        String extraHorizontalStr = "Sprinting/W Tap Horizontal: " + DECIMAL_FORMAT.format(kb.getExtraHorizontal());
        String verticalStr = "Vertical: " + DECIMAL_FORMAT.format(kb.getVertical());
        String extraVerticalStr = "Sprinting/W Tap Vertical: " + DECIMAL_FORMAT.format(kb.getExtraVertical());
        String verticalLimitStr = "Vertical Limit: " + DECIMAL_FORMAT.format(kb.getVerticalLimit());

        setSlot(0, ItemStacks.FRICTION.name(frictionStr).build(), interaction -> {
            if (interaction.getClickType() == ClickType.LEFT) {
                kb.knockbackFriction += 0.005;
            } else if (interaction.getClickType() == ClickType.RIGHT) {
                kb.knockbackFriction -= 0.005;
            } else if (interaction.getClickType() == ClickType.DROP) {
                kb.knockbackFriction = CustomKnockback.origKnockbackFriction;
            }
            reload();
        });

        setSlot(1, ItemStacks.HORIZONTAL.name(horizontalStr).build(), interaction -> {
            if (interaction.getClickType() == ClickType.LEFT) {
                kb.knockbackHorizontal += 0.005;
            } else if (interaction.getClickType() == ClickType.RIGHT) {
                kb.knockbackHorizontal -= 0.005;
            } else if (interaction.getClickType() == ClickType.DROP) {
                kb.knockbackHorizontal = CustomKnockback.origKnockbackHorizontal;
            }
            reload();
        });

        setSlot(2, ItemStacks.VERTICAL.name(verticalStr).build(), interaction -> {
            if (interaction.getClickType() == ClickType.LEFT) {
                kb.knockbackVertical += 0.005;
            } else if (interaction.getClickType() == ClickType.RIGHT) {
                kb.knockbackVertical -= 0.005;
            } else if (interaction.getClickType() == ClickType.DROP) {
                kb.knockbackVertical = CustomKnockback.origKnockbackVertical;
            }
            reload();
        });

        setSlot(4, ItemStacks.CLICK_TO_APPLY_CHANGES.name("Save Knockback").build(), interaction -> {
            Profile p = interaction.getProfile();
            p.getMatchData().setKnockback(kb);
            p.openMenu(menu);
        });

        setSlot(6, ItemStacks.EXTRA_HORIZONTAL
                .name(extraHorizontalStr).build(), interaction -> {
                    if (interaction.getClickType() == ClickType.LEFT) {
                        kb.knockbackExtraHorizontal += 0.005;
                    } else if (interaction.getClickType() == ClickType.RIGHT) {
                        kb.knockbackExtraHorizontal -= 0.005;
                    } else if (interaction.getClickType() == ClickType.DROP) {
                        kb.knockbackExtraHorizontal = CustomKnockback.origKnockbackExtraHorizontal;
                    }
                    reload();
                });

        setSlot(7, ItemStacks.EXTRA_VERTICAL.name(extraVerticalStr)
                .build(), interaction -> {
                    if (interaction.getClickType() == ClickType.LEFT) {
                        kb.knockbackExtraVertical += 0.005;
                    } else if (interaction.getClickType() == ClickType.RIGHT) {
                        kb.knockbackExtraVertical -= 0.005;
                    } else if (interaction.getClickType() == ClickType.DROP) {
                        kb.knockbackExtraVertical = CustomKnockback.origKnockbackExtraVertical;
                    }
                    reload();
                });

        setSlot(8, ItemStacks.VERTICAL_LIMIT.name(verticalLimitStr)
                .build(), interaction -> {
                    if (interaction.getClickType() == ClickType.LEFT) {
                        kb.knockbackVerticalLimit += 0.005;
                    } else if (interaction.getClickType() == ClickType.RIGHT) {
                        kb.knockbackVerticalLimit -= 0.005;
                    } else if (interaction.getClickType() == ClickType.DROP) {
                        kb.knockbackVerticalLimit = CustomKnockback.origKnockbackVerticalLimit;
                    }
                    reload();
                });

        return true;
    }

}
