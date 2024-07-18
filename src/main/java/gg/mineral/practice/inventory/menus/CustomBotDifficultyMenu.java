package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.bukkit.event.inventory.ClickType;

import gg.mineral.practice.bots.CustomDifficulty;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;

@ClickCancelled(true)
@RequiredArgsConstructor
public class CustomBotDifficultyMenu extends PracticeMenu {
        private final SelectGametypeMenu menu;
        final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

        static {
                DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
        }

        @Override
        public void update() {

                CustomDifficulty difficulty = viewer.getMatchData().getCustomBotDifficulty() == null
                                ? new CustomDifficulty()
                                : viewer.getMatchData().getCustomBotDifficulty();
                viewer.getMatchData().setCustomBotDifficulty(difficulty);
                viewer.getMatchData()
                                .setBotDifficulty(Difficulty.CUSTOM);

                setSlot(0, ItemStacks.AIM_SPEED.name(CC.SECONDARY + CC.B + "Aim Speed")
                                .lore(CC.WHITE + "The speed the bot " + CC.SECONDARY + "rotates" + CC.WHITE
                                                + " its head.", " ",
                                                CC.WHITE + "Horizontal:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getHorizontalAimSpeed()),
                                                CC.WHITE + "Vertical:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getVerticalAimSpeed()),
                                                CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                                                CC.RED + "Right click to change vertical.")
                                .build(), interaction -> {
                                        if (interaction.getClickType() == ClickType.RIGHT)
                                                interaction.getProfile().openMenu(
                                                                new ConfigureValueMenu<Float>(this, value -> difficulty
                                                                                .setVerticalAimSpeed(value),
                                                                                Float.class));
                                        else
                                                interaction.getProfile().openMenu(
                                                                ConfigureValueMenu.of(this, value -> difficulty
                                                                                .setHorizontalAimSpeed(value),
                                                                                float.class));
                                });

                setSlot(1, ItemStacks.AIM_ACCURACY.name(CC.SECONDARY + CC.B + "Aim Accuracy")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE
                                                + " the bot has when aiming.", " ",
                                                CC.WHITE + "Horizontal:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getHorizontalAimAccuracy()),
                                                CC.WHITE + "Vertical:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getVerticalAimAccuracy()),
                                                CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                                                CC.RED + "Right click to change vertical.")
                                .build(), interaction -> {
                                        if (interaction.getClickType() == ClickType.RIGHT)
                                                interaction.getProfile().openMenu(
                                                                ConfigureValueMenu.of(this, value -> difficulty
                                                                                .setVerticalAimAccuracy(value),
                                                                                float.class));
                                        else
                                                interaction.getProfile().openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setHorizontalAimAccuracy(value),
                                                                float.class));
                                });

                setSlot(2, ItemStacks.AIM_ERRATICNESS.name(CC.SECONDARY + CC.B + "Aim Erraticness")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "erraticness" + CC.WHITE
                                                + " the bot has when aiming.", " ",
                                                CC.WHITE + "Horizontal:",
                                                CC.GOLD + DECIMAL_FORMAT
                                                                .format(difficulty.getHorizontalAimErraticness()),
                                                CC.WHITE + "Vertical:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getVerticalAimErraticness()),
                                                CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                                                CC.RED + "Right click to change vertical.")
                                .build(), interaction -> {
                                        if (interaction.getClickType() == ClickType.RIGHT)
                                                interaction.getProfile().openMenu(
                                                                ConfigureValueMenu.of(this,
                                                                                value -> difficulty
                                                                                                .setVerticalAimErraticness(
                                                                                                                value),
                                                                                float.class));
                                        else
                                                interaction.getProfile().openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty
                                                                                .setHorizontalAimErraticness(value),
                                                                float.class));
                                });

                setSlot(3, ItemStacks.AIM_REACTION_TIME.name(CC.SECONDARY + CC.B + "Aim Reaction Time")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "reaction time" + CC.WHITE
                                                + " the bot has in ticks.",
                                                CC.WHITE + "One tick is equal to " + CC.SECONDARY + "50 milliseconds"
                                                                + CC.WHITE + ".",
                                                " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getReactionTimeTicks()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile().openMenu(
                                                ConfigureValueMenu.of(this,
                                                                value -> difficulty.setReactionTimeTicks(value),
                                                                float.class)));

                setSlot(4, ItemStacks.BOW_AIMING_RADIUS.name(CC.SECONDARY + CC.B + "Bow Aiming Radius")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "radius" + CC.WHITE
                                                + " around the player that ",
                                                CC.WHITE + "the bot will " + CC.SECONDARY + "target with the bow.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getBowAimingRadius()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setBowAimingRadius(value),
                                                                float.class)));

                setSlot(5, ItemStacks.REACH.name(CC.SECONDARY + CC.B + "Reach")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "distance" + CC.WHITE
                                                + " the bot can hit from.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getReach()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setReach(value), float.class)));

                setSlot(6, ItemStacks.SPRINT_RESET_ACCURACY.name(CC.SECONDARY + CC.B + "Sprint Reset Accuracy")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE
                                                + " the bot has when",
                                                CC.WHITE + "sprint resetting for the purpose of ",
                                                CC.WHITE + "dealing " + CC.SECONDARY + "more knockback"
                                                                + CC.WHITE
                                                                + ".",
                                                " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getSprintResetAccuracy()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile().openMenu(
                                                ConfigureValueMenu.of(this,
                                                                value -> difficulty.setSprintResetAccuracy(value),
                                                                float.class)));

                setSlot(7, ItemStacks.HIT_SELECT_ACCURACY.name(CC.SECONDARY + CC.B + "Hit Select Accuracy")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE
                                                + " the bot has when",
                                                CC.WHITE + "hit selecting for the purpose of ",
                                                CC.SECONDARY + "starting combos" + CC.WHITE
                                                                + ".",
                                                " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getHitSelectAccuracy()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile().openMenu(
                                                ConfigureValueMenu.of(this,
                                                                value -> difficulty.setHitSelectAccuracy(value),
                                                                float.class)));

                setSlot(8, ItemStacks.DISTANCING_MINIMUM.name(CC.SECONDARY + CC.B + "Distancing Minimum")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "minimum distance" + CC.WHITE
                                                + " the bot aims to ",
                                                CC.WHITE + "position itself at during combat.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getDistancingMin()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setDistancingMin(value),
                                                                float.class)));

                setSlot(9, ItemStacks.DISTANCING_MAXIMUM.name(CC.SECONDARY + CC.B + "Distancing Maximum")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "maximum distance" + CC.WHITE
                                                + " the bot aims to ",
                                                CC.WHITE + "position itself at during combat.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getDistancingMax()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setDistancingMax(value),
                                                                float.class)));

                setSlot(10, ItemStacks.CPS.name(CC.SECONDARY + CC.B + "CPS")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "amount of clicks" + CC.WHITE
                                                + " each second.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getCps()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setCps(value), float.class)));

                setSlot(11, ItemStacks.PING.name(CC.SECONDARY + CC.B + "Ping")
                                .lore(CC.WHITE + "Simulates the " + CC.SECONDARY + "amount of time", CC.WHITE
                                                + "it takes for packets to be",
                                                CC.SECONDARY + "transported" + CC.WHITE + ".", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getLatency()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setLatency(value), float.class)));

                setSlot(12, ItemStacks.PING_DEVIATION.name(CC.SECONDARY + CC.B + "Ping Deviation")
                                .lore(CC.WHITE + "Simulates the " + CC.SECONDARY + "variation in time", CC.WHITE
                                                + "it takes for packets to be",
                                                CC.SECONDARY + "transported" + CC.WHITE + ".", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getLatencyDeviation()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile().openMenu(
                                                ConfigureValueMenu.of(this,
                                                                value -> difficulty.setLatencyDeviation(value),
                                                                float.class)));

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
        }

        @Override
        public String getTitle() {
                return CC.BLUE + "Create Custom Difficulty";
        }

        @Override
        public boolean shouldUpdate() {
                return true;
        }

}
