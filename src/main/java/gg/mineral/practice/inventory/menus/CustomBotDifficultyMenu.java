package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.bukkit.event.inventory.ClickType;

import gg.mineral.practice.bots.CustomDifficulty;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

public class CustomBotDifficultyMenu extends PracticeMenu {
    SelectGametypeMenu menu;
    final static String TITLE = CC.BLUE + "Create Custom Difficulty";
    final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
    }

    public CustomBotDifficultyMenu(SelectGametypeMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {

        CustomDifficulty difficulty = viewer.getMatchData().getCustomBotDifficulty() == null ? new CustomDifficulty()
                : viewer.getMatchData().getCustomBotDifficulty();
        viewer.getMatchData().setCustomBotDifficulty(difficulty);
        viewer.getMatchData()
                .setBotDifficulty(Difficulty.CUSTOM);

        setSlot(0, ItemStacks.AIM_SPEED.name(CC.SECONDARY + CC.B + "Aim Speed")
                .lore(CC.WHITE + "The speed the bot " + CC.SECONDARY + "rotates" + CC.WHITE + " its head.", " ",
                        CC.WHITE + "Horizontal:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getHorizontalAimSpeed()),
                        CC.WHITE + "Vertical:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getVerticalAimSpeed()),
                        CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                        CC.RED + "Right click to change vertical.")
                .build(), interaction -> {
                    if (interaction.getClickType() == ClickType.RIGHT) {
                        interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                            difficulty.setVerticalAimSpeed(value);
                        }));
                    } else {
                        interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                            difficulty.setHorizontalAimSpeed(value);
                        }));
                    }
                });

        setSlot(1, ItemStacks.AIM_ACCURACY.name(CC.SECONDARY + CC.B + "Aim Accuracy")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE + " the bot has when aiming.", " ",
                        CC.WHITE + "Horizontal:",
                        CC.GOLD + DECIMAL_FORMAT.format(difficulty.getHorizontalAimAccuracy()),
                        CC.WHITE + "Vertical:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getVerticalAimAccuracy()),
                        CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                        CC.RED + "Right click to change vertical.")
                .build(), interaction -> {
                    if (interaction.getClickType() == ClickType.RIGHT) {
                        interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                            difficulty.setVerticalAimAccuracy(value);
                        }));
                    } else {
                        interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                            difficulty.setHorizontalAimAccuracy(value);
                        }));
                    }
                });

        setSlot(2, ItemStacks.AIM_REACTION_TIME.name(CC.SECONDARY + CC.B + "Aim Reaction Time")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "reaction time" + CC.WHITE
                        + " the bot has in ticks.",
                        CC.WHITE + "One tick is equal to " + CC.SECONDARY + "50 milliseconds" + CC.WHITE + ".", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getReactionTimeTicks()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setReactionTimeTicks(value);
                    }));
                });

        setSlot(3, ItemStacks.BOW_AIMING_RADIUS.name(CC.SECONDARY + CC.B + "Bow Aiming Radius")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "radius" + CC.WHITE + " around the player that ",
                        CC.WHITE + "the bot will " + CC.SECONDARY + "target with the bow.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getBowAimingRadius()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setBowAimingRadius(value);
                    }));
                });

        setSlot(4, ItemStacks.REACH.name(CC.SECONDARY + CC.B + "Reach")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "distance" + CC.WHITE + " the bot can hit from.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getReach()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setReach(value);
                    }));
                });

        setSlot(5, ItemStacks.SPRINT_RESET_ACCURACY.name(CC.SECONDARY + CC.B + "Sprint Reset Accuracy")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE
                        + " the bot has when",
                        CC.WHITE + "sprint resetting for the purpose of ",
                        CC.WHITE + "dealing " + CC.SECONDARY + "more knockback"
                                + CC.WHITE
                                + ".",
                        " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getSprintResetAccuracy()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setSprintResetAccuracy(value);
                    }));
                });

        setSlot(6, ItemStacks.HIT_SELECT_ACCURACY.name(CC.SECONDARY + CC.B + "Hit Select Accuracy")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE
                        + " the bot has when",
                        CC.WHITE + "hit selecting for the purpose of ", CC.SECONDARY + "starting combos" + CC.WHITE
                                + ".",
                        " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getHitSelectAccuracy()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setHitSelectAccuracy(value);
                    }));
                });

        setSlot(7, ItemStacks.DISTANCING_MINIMUM.name(CC.SECONDARY + CC.B + "Distancing Minimum")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "minimum distance" + CC.WHITE + " the bot aims to ",
                        CC.WHITE + "position itself at during combat.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getDistancingMin()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setDistancingMin(value);
                    }));
                });

        setSlot(8, ItemStacks.DISTANCING_MAXIMUM.name(CC.SECONDARY + CC.B + "Distancing Maximum")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "maximum distance" + CC.WHITE + " the bot aims to ",
                        CC.WHITE + "position itself at during combat.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getDistancingMax()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setDistancingMax(value);
                    }));
                });

        setSlot(9, ItemStacks.CPS.name(CC.SECONDARY + CC.B + "CPS")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "amount of clicks" + CC.WHITE + " each second.", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getCps()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setCps(value);
                    }));
                });

        setSlot(10, ItemStacks.PING.name(CC.SECONDARY + CC.B + "Ping")
                .lore(CC.WHITE + "Simulates the " + CC.SECONDARY + "amount of time", CC.WHITE
                        + "it takes for packets to be", CC.SECONDARY + "transported" + CC.WHITE + ".", " ",
                        CC.WHITE + "Currently:", CC.GOLD + DECIMAL_FORMAT.format(difficulty.getLatency()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(), interaction -> {
                    interaction.getProfile().openMenu(new ConfigureDifficultyValueMenu(this, value -> {
                        difficulty.setLatency(value);
                    }));
                });

        setSlot(29, ItemStacks.BACK, interaction -> {
            Profile p = interaction.getProfile();
            p.getMatchData().setCustomBotDifficulty(difficulty);
            p.openMenu(menu);
        });

        setSlot(31, ItemStacks.CLICK_TO_APPLY_CHANGES.name(CC.SECONDARY + CC.B + "Save Difficulty").build(),
                interaction -> {
                    Profile p = interaction.getProfile();
                    p.getMatchData().setCustomBotDifficulty(difficulty);
                    p.openMenu(menu);
                });

        setSlot(33, ItemStacks.RANDOM_DIFFICULTY, interaction -> {
            Profile p = interaction.getProfile();
            difficulty.randomize();
            p.getMatchData().setCustomBotDifficulty(difficulty);
            reload();
        });

        return true;
    }

}
