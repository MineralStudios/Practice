package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.match.knockback.CustomKnockback
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import gg.mineral.server.combat.KnockbackProfile
import gg.mineral.server.combat.KnockbackProfileList
import java.math.RoundingMode
import java.text.DecimalFormat

@ClickCancelled(true)
class CreateCustomKnockbackMenu(private val menu: Menu, private val prevMenu: Menu) :
    PracticeMenu() {
    private val profiles: Array<KnockbackProfile> = KnockbackProfileList.getProfiles().values
        .sortedWith { profile1, profile2 ->
            if ("default_kb" == profile1.name) return@sortedWith -1
            if ("default_kb" == profile2.name) return@sortedWith 1
            0
        }.toTypedArray()
    private var kbIndex = 0
    private var oldCombat = false
    private var customKnockback: CustomKnockback? = null

    private fun setFriction(friction: Double) {
        kbIndex = -1
        customKnockback?.friction = friction
    }

    private fun setHorizontal(horizontal: Double) {
        kbIndex = -1
        customKnockback?.horizontal = horizontal
    }

    private fun setVertical(vertical: Double) {
        kbIndex = -1
        customKnockback?.vertical = vertical
    }

    private fun setHorizontalExtra(horizontalExtra: Double) {
        kbIndex = -1
        customKnockback?.horizontalExtra = horizontalExtra
    }

    private fun setVerticalExtra(verticalExtra: Double) {
        kbIndex = -1
        customKnockback?.verticalExtra = verticalExtra
    }

    private fun setVerticalLimit(verticalLimit: Double) {
        kbIndex = -1
        customKnockback?.verticalLimit = verticalLimit
    }

    private fun getFriction(kb: KnockbackProfile): Double {
        if (kbIndex > 0) return kb.configValues.getOrDefault("friction", customKnockback!!.friction) as Double
        return customKnockback!!.friction
    }

    private fun getHorizontal(kb: KnockbackProfile): Double {
        if (kbIndex > 0) return kb.configValues.getOrDefault("horizontal", customKnockback!!.horizontal) as Double
        return customKnockback!!.horizontal
    }

    private fun getVertical(kb: KnockbackProfile): Double {
        if (kbIndex > 0) return kb.configValues.getOrDefault("vertical", customKnockback!!.vertical) as Double
        return customKnockback!!.vertical
    }

    private fun getHorizontalExtra(kb: KnockbackProfile): Double {
        if (kbIndex > 0) return kb.configValues.getOrDefault(
            "horizontalExtra",
            customKnockback!!.horizontalExtra
        ) as Double
        return customKnockback!!.horizontalExtra
    }

    private fun getVerticalExtra(kb: KnockbackProfile): Double {
        if (kbIndex > 0) return kb.configValues.getOrDefault("verticalExtra", customKnockback!!.verticalExtra) as Double
        return customKnockback!!.verticalExtra
    }

    private fun getVerticalLimit(kb: KnockbackProfile): Double {
        if (kbIndex > 0) return kb.configValues.getOrDefault("verticalLimit", customKnockback!!.verticalLimit) as Double
        return customKnockback!!.verticalLimit
    }

    override fun update() {
        val duelSettings = viewer.duelSettings

        for (i in 0..profiles.size) {
            if (kbIndex >= 0 && profiles[kbIndex].name.contains("combo_kb")) {
                kbIndex++
                if (kbIndex >= profiles.size || kbIndex < 0) kbIndex = 0
                continue
            }
            break
        }

        val kb = if (kbIndex >= 0 && kbIndex < profiles.size) profiles[kbIndex] else null

        if (customKnockback == null) {
            customKnockback = if (duelSettings.knockback is CustomKnockback)
                CustomKnockback(duelSettings.knockback as CustomKnockback)
            else
                CustomKnockback()

            if (kbIndex >= 0 && kbIndex < profiles.size) {
                val friction = profiles[kbIndex].configValues.getOrDefault("friction", customKnockback!!.friction)
                val horizontal = profiles[kbIndex].configValues.getOrDefault("horizontal", customKnockback!!.horizontal)
                val vertical = profiles[kbIndex].configValues.getOrDefault("vertical", customKnockback!!.vertical)
                val horizontalExtra =
                    profiles[kbIndex].configValues.getOrDefault("horizontalExtra", customKnockback!!.horizontalExtra)
                val verticalExtra =
                    profiles[kbIndex].configValues.getOrDefault("verticalExtra", customKnockback!!.verticalExtra)
                val verticalLimit =
                    profiles[kbIndex].configValues.getOrDefault("verticalLimit", customKnockback!!.verticalLimit)
                val oldCombat = profiles[kbIndex].configValues.getOrDefault("oldCombat", duelSettings.oldCombat)
                customKnockback!!.friction = friction as Double
                customKnockback!!.horizontal = horizontal as Double
                customKnockback!!.vertical = vertical as Double
                customKnockback!!.horizontalExtra = horizontalExtra as Double
                customKnockback!!.verticalExtra = verticalExtra as Double
                customKnockback!!.verticalLimit = verticalLimit as Double
                this.oldCombat = oldCombat as Boolean
            }
        }

        setSlot(
            10, ItemStacks.FRICTION.name(CC.SECONDARY + CC.B + "Friction")
                .lore(
                    CC.WHITE + "The amount " + CC.SECONDARY + "movement speed and direction",
                    CC.WHITE + "influences knockback magnitude.", " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(getFriction(customKnockback!!)),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { friction: Double -> this.setFriction(friction) },
                        Double::class.javaPrimitiveType!!
                    )
                )
        }

        setSlot(
            11, ItemStacks.HORIZONTAL.name(CC.SECONDARY + CC.B + "Horizontal")
                .lore(
                    CC.WHITE + "The " + CC.SECONDARY + "horizontal" + CC.WHITE + " knockback.", " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(getHorizontal(customKnockback!!)),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { horizontal: Double -> this.setHorizontal(horizontal) },
                        Double::class.javaPrimitiveType!!
                    )
                )
        }

        setSlot(
            12, ItemStacks.VERTICAL.name(CC.SECONDARY + CC.B + "Vertical")
                .lore(
                    CC.WHITE + "The " + CC.SECONDARY + "vertical" + CC.WHITE + " knockback.", " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(getVertical(customKnockback!!)),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { vertical: Double -> this.setVertical(vertical) },
                        Double::class.javaPrimitiveType!!
                    )
                )
        }

        setSlot(
            13, ItemStacks.EXTRA_HORIZONTAL
                .name(CC.SECONDARY + CC.B + "Extra Horizontal")
                .lore(
                    CC.WHITE + "The " + CC.SECONDARY + "horizontal knockback" + CC.WHITE + " added",
                    CC.WHITE + "when sprinting/sprint resetting.", " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(getHorizontalExtra(customKnockback!!)),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { horizontalExtra: Double -> this.setHorizontalExtra(horizontalExtra) },
                        Double::class.javaPrimitiveType!!
                    )
                )
        }

        setSlot(
            14, ItemStacks.EXTRA_VERTICAL.name(CC.SECONDARY + CC.B + "Extra Vertical")
                .lore(
                    CC.WHITE + "The " + CC.SECONDARY + "vertical knockback" + CC.WHITE + " added",
                    CC.WHITE + "when sprinting/sprint resetting.", " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(getVerticalExtra(customKnockback!!)),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { verticalExtra: Double -> this.setVerticalExtra(verticalExtra) },
                        Double::class.javaPrimitiveType!!
                    )
                )
        }

        setSlot(
            15, ItemStacks.VERTICAL_LIMIT.name(CC.SECONDARY + CC.B + "Vertical Limit")
                .lore(
                    CC.WHITE + "The " + CC.SECONDARY + "limit" + CC.WHITE + " to vertical knockback.",
                    " ",
                    CC.WHITE + "Currently:",
                    CC.GOLD + DECIMAL_FORMAT.format(getVerticalLimit(customKnockback!!)),
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { verticalLimit: Double -> this.setVerticalLimit(verticalLimit) },
                        Double::class.javaPrimitiveType!!
                    )
                )
        }

        setSlot(
            16, ItemStacks.OLD_COMBAT.name(CC.SECONDARY + CC.B + "Delayed Combat").lore(
                (CC.WHITE + "Play using " + CC.SECONDARY + "delayed combat" + CC.WHITE
                        + " seen on servers from 2015-2017."),
                " ",
                CC.WHITE + "Currently:", if (this.oldCombat) CC.GREEN + "Enabled" else CC.RED + "Disabled", " ",
                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle delayed combat."
            ).build()
        ) {
            this.oldCombat =
                !this.oldCombat
            reload()
        }

        setSlot(
            29, ItemStacks.BACK
        ) { interaction: Interaction -> interaction.profile.openMenu(prevMenu) }

        setSlot(31, ItemStacks.APPLY) { interaction: Interaction ->
            val p = interaction.profile
            p.duelSettings.oldCombat = oldCombat
            p.duelSettings.knockback = kb ?: customKnockback
            p.openMenu(menu)
        }

        setSlot(
            33,
            ItemStacks.CHOOSE_EXISTING_KNOCKBACK.lore(
                (CC.WHITE + "Choose a " + CC.SECONDARY + "preconfigured knockback"
                        + CC.WHITE + "."),
                " ",
                CC.WHITE + "Currently: " + CC.GOLD + (if (kb == null)
                    "Custom"
                else
                    kb.name),
                " ",
                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select knockback."
            ).build()
        ) {
            val lastKbIndex = this.kbIndex
            for (i in 0..profiles.size) {
                kbIndex++

                if (kbIndex >= profiles.size || kbIndex < 0) kbIndex = 0

                if (profiles[kbIndex].name.contains("combo_kb")) continue
                break
            }

            if (lastKbIndex != kbIndex && kbIndex >= 0 && kbIndex < profiles.size) {
                val friction =
                    profiles[kbIndex].configValues.getOrDefault("friction", customKnockback!!.friction) as Double
                val horizontal = profiles[kbIndex].configValues.getOrDefault(
                    "horizontal",
                    customKnockback!!.horizontal
                ) as Double
                val vertical =
                    profiles[kbIndex].configValues.getOrDefault("vertical", customKnockback!!.vertical) as Double
                val horizontalExtra = profiles[kbIndex].configValues.getOrDefault(
                    "horizontalExtra",
                    customKnockback!!.horizontalExtra
                ) as Double
                val verticalExtra = profiles[kbIndex].configValues.getOrDefault(
                    "verticalExtra",
                    customKnockback!!.verticalExtra
                ) as Double
                val verticalLimit = profiles[kbIndex].configValues.getOrDefault(
                    "verticalLimit",
                    customKnockback!!.verticalLimit
                ) as Double
                val oldCombat =
                    profiles[kbIndex].configValues.getOrDefault("oldCombat", duelSettings.oldCombat) as Boolean
                customKnockback!!.friction = friction
                customKnockback!!.horizontal = horizontal
                customKnockback!!.vertical = vertical
                customKnockback!!.horizontalExtra = horizontalExtra
                customKnockback!!.verticalExtra = verticalExtra
                customKnockback!!.verticalLimit = verticalLimit
                this.oldCombat = oldCombat
            }
            reload()
        }
    }

    override val title: String
        get() = CC.BLUE + "Create Custom Knockback"

    override fun shouldUpdate() = true

    companion object {
        private val DECIMAL_FORMAT = DecimalFormat("#.###")

        init {
            DECIMAL_FORMAT.roundingMode = RoundingMode.HALF_DOWN
        }
    }
}
