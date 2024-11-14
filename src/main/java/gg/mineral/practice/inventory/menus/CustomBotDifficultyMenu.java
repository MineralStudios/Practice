package gg.mineral.practice.inventory.menus;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.bukkit.event.inventory.ClickType;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.practice.bots.Difficulty;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.RequiredArgsConstructor;
import lombok.val;

@ClickCancelled(true)
@RequiredArgsConstructor
public class CustomBotDifficultyMenu extends PracticeMenu {
    private final SelectGametypeMenu menu;
    final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.###");
    private Difficulty premadeDifficulty = Difficulty.EASY;
    private BotConfiguration difficulty = Difficulty.EASY.getConfiguration(null);

    static {
        DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
    }

    @Override
    public void update() {

        val queueSettings = viewer.getQueueSettings();

        queueSettings.setOpponentDifficulty((byte) Difficulty.CUSTOM.ordinal());

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
                                ConfigureValueMenu.of(this, value -> difficulty
                                        .setVerticalAimSpeed(value),
                                        float.class));
                    else
                        interaction.getProfile().openMenu(
                                ConfigureValueMenu.of(this, value -> difficulty
                                        .setHorizontalAimSpeed(value),
                                        float.class));
                    premadeDifficulty = Difficulty.CUSTOM;
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
                    premadeDifficulty = Difficulty.CUSTOM;
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
                    premadeDifficulty = Difficulty.CUSTOM;
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
                interaction -> {
                    interaction.getProfile().openMenu(
                            ConfigureValueMenu.of(this,
                                    value -> difficulty
                                            .setSprintResetAccuracy(value),
                                    float.class));
                    premadeDifficulty = Difficulty.CUSTOM;
                });

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
                interaction -> {
                    interaction.getProfile().openMenu(
                            ConfigureValueMenu.of(this,
                                    value -> difficulty.setHitSelectAccuracy(value),
                                    float.class));
                    premadeDifficulty = Difficulty.CUSTOM;
                });

        setSlot(5, ItemStacks.CPS.name(CC.SECONDARY + CC.B + "CPS")
                .lore(CC.WHITE + "The " + CC.SECONDARY + "amount of clicks" + CC.WHITE
                        + " each second.", " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(difficulty.getAverageCps()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> {
                    interaction.getProfile()
                            .openMenu(ConfigureValueMenu.of(this,
                                    value -> difficulty.setAverageCps(value),
                                    int.class));
                    premadeDifficulty = Difficulty.CUSTOM;
                });

        setSlot(6, ItemStacks.PING.name(CC.SECONDARY + CC.B + "Ping")
                .lore(CC.WHITE + "Simulates the " + CC.SECONDARY + "amount of time", CC.WHITE
                        + "it takes for packets to be",
                        CC.SECONDARY + "transported" + CC.WHITE + ".", " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(difficulty.getLatency()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> {
                    interaction.getProfile()
                            .openMenu(ConfigureValueMenu.of(this,
                                    value -> difficulty.setLatency(value),
                                    int.class));
                    premadeDifficulty = Difficulty.CUSTOM;
                });

        setSlot(7, ItemStacks.PING_DEVIATION.name(CC.SECONDARY + CC.B + "Ping Deviation")
                .lore(CC.WHITE + "Simulates the " + CC.SECONDARY + "variation in time", CC.WHITE
                        + "it takes for packets to be",
                        CC.SECONDARY + "transported" + CC.WHITE + ".", " ",
                        CC.WHITE + "Currently:",
                        CC.GOLD + DECIMAL_FORMAT.format(difficulty.getLatencyDeviation()),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                .build(),
                interaction -> {
                    interaction.getProfile().openMenu(
                            ConfigureValueMenu.of(this,
                                    value -> difficulty.setLatencyDeviation(value),
                                    int.class));
                    premadeDifficulty = Difficulty.CUSTOM;
                });

        setSlot(29, ItemStacks.BACK, interaction -> interaction.getProfile().openMenu(menu));

        setSlot(31, ItemStacks.CLICK_TO_APPLY_CHANGES.name(CC.SECONDARY + CC.B + "Save Difficulty").build(),
                interaction -> {
                    val p = interaction.getProfile();
                    p.getQueueSettings()
                            .setCustomBotConfiguration(difficulty);
                    p.getQueueSettings().setOpponentDifficulty((byte) premadeDifficulty.ordinal());
                    p.openMenu(menu);
                });

        setSlot(33, ItemStacks.PREMADE_DIFFICULTY.lore(
                CC.WHITE + "Allows you to select a " + CC.SECONDARY + "premade difficulty"
                        + CC.WHITE
                        + ".",
                " ", CC.WHITE + "Selected Difficulty: ",
                premadeDifficulty.getDisplay(), " ",
                CC.BOARD_SEPARATOR, " ", CC.GREEN + "Left Click to change difficulty.",
                CC.RED + "Right Click to choose random difficulty.").build(),
                interaction -> {
                    if (interaction.getClickType() == ClickType.RIGHT) {
                        premadeDifficulty = Difficulty.RANDOM;
                    } else {
                        premadeDifficulty = Difficulty
                                .values()[(premadeDifficulty.ordinal() + 1)
                                        % Difficulty.values().length];
                        if (premadeDifficulty == Difficulty.CUSTOM)
                            premadeDifficulty = Difficulty
                                    .values()[(premadeDifficulty.ordinal() + 1)
                                            % Difficulty.values().length];
                        if (premadeDifficulty == Difficulty.RANDOM)
                            premadeDifficulty = Difficulty
                                    .values()[(premadeDifficulty.ordinal() + 1)
                                            % Difficulty.values().length];
                    }
                    difficulty = premadeDifficulty.getConfiguration(queueSettings);
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
