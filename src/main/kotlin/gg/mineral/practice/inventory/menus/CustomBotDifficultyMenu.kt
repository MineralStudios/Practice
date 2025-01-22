package gg.mineral.practice.inventory.menus

import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.practice.bots.Difficulty
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import org.bukkit.event.inventory.ClickType
import java.math.RoundingMode
import java.text.DecimalFormat

@ClickCancelled(true)
class CustomBotDifficultyMenu(private val menu: SelectGametypeMenu) : PracticeMenu() {
    private var premadeDifficulty = Difficulty.CUSTOM
    private var difficulty: BotConfiguration? = null

    override fun update() {
        val queueSettings = viewer.queueSettings

        if (this.difficulty == null) {
            val savedDiff = queueSettings.customBotConfiguration

            for (diff in Difficulty.entries) {
                if (diff.configEquals(savedDiff)) {
                    premadeDifficulty = diff
                    break
                }
            }

            this.difficulty = premadeDifficulty.getConfiguration(queueSettings)
        }

        setSlot(
            10, ItemStacks.AIM_SPEED.name(CC.SECONDARY + CC.B + "Aim Speed")
                .lore(
                    (CC.WHITE + "The speed the bot " + CC.SECONDARY + "rotates" + CC.WHITE
                            + " its head."), " ",
                    CC.WHITE + "Horizontal:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.horizontalAimSpeed.toDouble()),
                    CC.WHITE + "Vertical:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.verticalAimSpeed.toDouble()),
                    CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                    CC.RED + "Right click to change vertical."
                )
                .build()
        ) { interaction: Interaction ->
            if (interaction.clickType == ClickType.RIGHT) interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this, { value -> difficulty!!.verticalAimSpeed = value },
                    Float::class
                )
            )
            else interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this, { value -> difficulty!!.horizontalAimSpeed = value },
                    Float::class
                )
            )
            premadeDifficulty = Difficulty.CUSTOM
        }

        setSlot(
            11, ItemStacks.AIM_ACCURACY.name(CC.SECONDARY + CC.B + "Aim Accuracy")
                .lore(
                    (CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE
                            + " the bot has when aiming."), " ",
                    CC.WHITE + "Horizontal:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.horizontalAimAccuracy.toDouble()),
                    CC.WHITE + "Vertical:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.verticalAimAccuracy.toDouble()),
                    CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                    CC.RED + "Right click to change vertical."
                )
                .build()
        ) { interaction: Interaction ->
            if (interaction.clickType == ClickType.RIGHT) interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this, { value -> difficulty!!.verticalAimAccuracy = value },
                    Float::class
                )
            )
            else interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this,
                    { value -> difficulty!!.horizontalAimAccuracy = value },
                    Float::class
                )
            )
            premadeDifficulty = Difficulty.CUSTOM
        }

        setSlot(
            12, ItemStacks.AIM_ERRATICNESS.name(CC.SECONDARY + CC.B + "Aim Erraticness")
                .lore(
                    (CC.WHITE + "The " + CC.SECONDARY + "erraticness" + CC.WHITE
                            + " the bot has when aiming."), " ",
                    CC.WHITE + "Horizontal:",
                    CC.GOLD + DECIMAL_FORMAT
                        .format(difficulty!!.horizontalErraticness.toDouble()),
                    CC.WHITE + "Vertical:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.verticalErraticness.toDouble()),
                    CC.BOARD_SEPARATOR, CC.GREEN + "Left click to change horizontal.",
                    CC.RED + "Right click to change vertical."
                )
                .build()
        ) { interaction: Interaction ->
            if (interaction.clickType == ClickType.RIGHT) interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this,
                    { value -> difficulty!!.verticalErraticness = value },
                    Float::class
                )
            )
            else interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this,
                    { value -> difficulty!!.horizontalErraticness = value },
                    Float::class
                )
            )
            premadeDifficulty = Difficulty.CUSTOM
        }

        setSlot(
            13, ItemStacks.SPRINT_RESET_ACCURACY.name(CC.SECONDARY + CC.B + "Sprint Reset Accuracy")
                .lore(
                    (CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE
                            + " the bot has when"),
                    CC.WHITE + "sprint resetting for the purpose of ",
                    (CC.WHITE + "dealing " + CC.SECONDARY + "more knockback"
                            + CC.WHITE
                            + "."),
                    " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.sprintResetAccuracy.toDouble()),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this,
                    { value -> difficulty!!.sprintResetAccuracy = value },
                    Float::class
                )
            )
            premadeDifficulty = Difficulty.CUSTOM
        }

        setSlot(
            14, ItemStacks.HIT_SELECT_ACCURACY.name(CC.SECONDARY + CC.B + "Hit Select Accuracy")
                .lore(
                    (CC.WHITE + "The " + CC.SECONDARY + "accuracy" + CC.WHITE
                            + " the bot has when"),
                    CC.WHITE + "hit selecting for the purpose of ",
                    (CC.SECONDARY + "starting combos" + CC.WHITE
                            + "."),
                    " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.hitSelectAccuracy.toDouble()),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this,
                    { value -> difficulty!!.hitSelectAccuracy = value },
                    Float::class
                )
            )
            premadeDifficulty = Difficulty.CUSTOM
        }

        setSlot(
            15, ItemStacks.CPS.name(CC.SECONDARY + CC.B + "CPS")
                .lore(
                    (CC.WHITE + "The " + CC.SECONDARY + "amount of clicks" + CC.WHITE
                            + " each second."), " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.averageCps.toDouble()),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { value -> difficulty!!.averageCps = value.toFloat() },
                        Int::class
                    )
                )
            premadeDifficulty = Difficulty.CUSTOM
        }

        setSlot(
            16, ItemStacks.PING.name(CC.SECONDARY + CC.B + "Ping")
                .lore(
                    CC.WHITE + "Simulates the " + CC.SECONDARY + "amount of time", CC.WHITE
                            + "it takes for packets to be",
                    CC.SECONDARY + "transported" + CC.WHITE + ".", " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.latency.toLong()),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { value -> difficulty!!.latency = value },
                        Int::class
                    )
                )
            premadeDifficulty = Difficulty.CUSTOM
        }

        setSlot(
            19, ItemStacks.PING_DEVIATION.name(CC.SECONDARY + CC.B + "Ping Deviation")
                .lore(
                    CC.WHITE + "Simulates the " + CC.SECONDARY + "variation in time", CC.WHITE
                            + "it takes for packets to be",
                    CC.SECONDARY + "transported" + CC.WHITE + ".", " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(difficulty!!.latencyDeviation.toLong()),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                ConfigureValueMenu.of(
                    this,
                    { value -> difficulty!!.latencyDeviation = value },
                    Int::class
                )
            )
            premadeDifficulty = Difficulty.CUSTOM
        }

        setSlot(
            38, ItemStacks.BACK
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                menu
            )
        }

        setSlot(
            40, ItemStacks.CLICK_TO_APPLY_CHANGES.name(CC.SECONDARY + CC.B + "Save Difficulty").build()
        ) { interaction: Interaction ->
            val p = interaction.profile
            difficulty?.let { p.queueSettings.customBotConfiguration = it }
            p.queueSettings.opponentDifficulty = premadeDifficulty.ordinal.toByte()
            p.openMenu(menu)
        }

        setSlot(
            42, ItemStacks.PREMADE_DIFFICULTY.lore(
                (CC.WHITE + "Allows you to select a " + CC.SECONDARY + "premade difficulty"
                        + CC.WHITE
                        + "."),
                " ", CC.WHITE + "Selected Difficulty: ",
                premadeDifficulty.display, " ",
                CC.BOARD_SEPARATOR, " ", CC.GREEN + "Left Click to change difficulty.",
                CC.RED + "Right Click to choose random difficulty."
            ).build()
        ) { interaction: Interaction ->
            if (interaction.clickType == ClickType.RIGHT) {
                premadeDifficulty = Difficulty.RANDOM
            } else {
                premadeDifficulty = Difficulty.entries[(premadeDifficulty.ordinal + 1)
                        % Difficulty.entries.size]
                if (premadeDifficulty === Difficulty.CUSTOM) premadeDifficulty =
                    Difficulty.entries[(premadeDifficulty.ordinal + 1)
                            % Difficulty.entries.size]
                if (premadeDifficulty === Difficulty.RANDOM) premadeDifficulty =
                    Difficulty.entries[(premadeDifficulty.ordinal + 1)
                            % Difficulty.entries.size]
            }
            difficulty = premadeDifficulty.getConfiguration(queueSettings)
            reload()
        }
    }

    override val title: String
        get() = CC.BLUE + "Create Custom Difficulty"

    override fun shouldUpdate(): Boolean {
        return true
    }

    companion object {
        val DECIMAL_FORMAT: DecimalFormat = DecimalFormat("#.###")

        init {
            DECIMAL_FORMAT.roundingMode = RoundingMode.HALF_DOWN
        }
    }
}
