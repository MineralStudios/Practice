package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.bukkit.event.inventory.ClickType;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.practice.bots.Difficulty;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.queue.QueueSettings;
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

                QueueSettings queueSettings = viewer.getQueueSettings();

                BotConfiguration difficulty = queueSettings.getCustomBotConfiguration();
                queueSettings.setCustomBotConfiguration(difficulty);
                queueSettings
                                .setOpponentDifficulty(0, Difficulty.CUSTOM);

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
                                                                .format(difficulty.getHorizontalErraticness()),
                                                CC.WHITE + "Vertical:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getVerticalErraticness()),
                                                CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                                                CC.RED + "Right click to change vertical.")
                                .build(), interaction -> {
                                        if (interaction.getClickType() == ClickType.RIGHT)
                                                interaction.getProfile().openMenu(
                                                                ConfigureValueMenu.of(this,
                                                                                value -> difficulty
                                                                                                .setVerticalErraticness(
                                                                                                                value),
                                                                                float.class));
                                        else
                                                interaction.getProfile().openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty
                                                                                .setHorizontalErraticness(value),
                                                                float.class));
                                });

                setSlot(3, ItemStacks.SPRINT_RESET_ACCURACY.name(CC.SECONDARY + CC.B + "Sprint Reset Accuracy")
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

                setSlot(4, ItemStacks.HIT_SELECT_ACCURACY.name(CC.SECONDARY + CC.B + "Hit Select Accuracy")
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

                setSlot(5, ItemStacks.CPS.name(CC.SECONDARY + CC.B + "CPS")
                                .lore(CC.WHITE + "The " + CC.SECONDARY + "amount of clicks" + CC.WHITE
                                                + " each second.", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getAverageCps()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setAverageCps(value), int.class)));

                setSlot(6, ItemStacks.PING.name(CC.SECONDARY + CC.B + "Ping")
                                .lore(CC.WHITE + "Simulates the " + CC.SECONDARY + "amount of time", CC.WHITE
                                                + "it takes for packets to be",
                                                CC.SECONDARY + "transported" + CC.WHITE + ".", " ",
                                                CC.WHITE + "Currently:",
                                                CC.GOLD + DECIMAL_FORMAT.format(difficulty.getLatency()),
                                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                                .build(),
                                interaction -> interaction.getProfile()
                                                .openMenu(ConfigureValueMenu.of(this,
                                                                value -> difficulty.setLatency(value), int.class)));

                setSlot(7, ItemStacks.PING_DEVIATION.name(CC.SECONDARY + CC.B + "Ping Deviation")
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
                                                                int.class)));

                setSlot(29, ItemStacks.BACK, interaction -> {
                        Profile p = interaction.getProfile();
                        interaction.getProfile().getQueueSettings().setCustomBotConfiguration(difficulty);
                        p.openMenu(menu);
                });

                setSlot(31, ItemStacks.CLICK_TO_APPLY_CHANGES.name(CC.SECONDARY + CC.B + "Save Difficulty").build(),
                                interaction -> {
                                        Profile p = interaction.getProfile();
                                        interaction.getProfile().getQueueSettings()
                                                        .setCustomBotConfiguration(difficulty);
                                        p.openMenu(menu);
                                });

                setSlot(33, ItemStacks.RANDOM_DIFFICULTY, interaction -> {
                        QueueSettings queueSettings1 = interaction.getProfile().getQueueSettings();
                        queueSettings1.setCustomBotConfiguration(Difficulty.RANDOM.getConfiguration(queueSettings1));
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
