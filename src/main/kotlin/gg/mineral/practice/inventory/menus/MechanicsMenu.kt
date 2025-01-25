package gg.mineral.practice.inventory.menus

import gg.mineral.practice.inventory.*
import gg.mineral.practice.managers.ArenaManager.arenas
import gg.mineral.practice.managers.GametypeManager.gametypes
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import gg.mineral.server.combat.KnockbackProfileList

@ClickCancelled(true)
class MechanicsMenu(private val prevMenu: Menu? = null, val submitAction: SubmitAction) : PracticeMenu() {

    override fun update() {
        clear()
        val duelSettings = viewer.duelSettings
        val noDamageTicks = duelSettings.noDamageTicks
        val knockback = if (duelSettings.knockback == null)
            if (noDamageTicks < 10)
                KnockbackProfileList.getComboKnockbackProfile()
            else
                KnockbackProfileList.getDefaultKnockbackProfile()
        else
            duelSettings.knockback

        val kit = duelSettings.kit ?: gametypes[0.toByte()].kit

        setSlot(
            10,
            ItemStacks.SELECT_KIT
                .lore(
                    CC.WHITE + "The " + CC.SECONDARY + "items" + CC.WHITE + " in your inventory.", " ",
                    CC.WHITE + "Currently:", CC.GOLD + kit.name,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change kit."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                SelectKitMenu(this)
            )
        }

        setSlot(
            11,
            ItemStacks.CHANGE_KNOCKBACK
                .lore(
                    (CC.WHITE + "Changes the amount of" + CC.SECONDARY + " knockback" + CC.WHITE
                            + " you recieve."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + knockback!!.name,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change knockback."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                CreateCustomKnockbackMenu(this, this)
            )
        }

        setSlot(
            12, ItemStacks.HIT_DELAY
                .lore(
                    (CC.WHITE + "Changes how " + CC.SECONDARY + "frequently " + CC.WHITE
                            + "you can attack."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + noDamageTicks + " Ticks",
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change hit delay."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { duelSettings.noDamageTicks = it },
                        Int::class
                    )
                )
        }

        setSlot(
            13, ItemStacks.TOGGLE_HUNGER
                .lore(
                    (CC.WHITE + "Changes if you lose " + CC.SECONDARY + "hunger" + CC.WHITE
                            + "."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + duelSettings.hunger,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle hunger."
                )
                .build()
        ) {
            duelSettings.hunger = !duelSettings.hunger
            reload()
        }

        setSlot(
            14, ItemStacks.TOGGLE_BUILD
                .lore(
                    (CC.WHITE + "Changes if you can " + CC.SECONDARY + "build" + CC.WHITE
                            + "."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + duelSettings.build,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle build."
                )
                .build()
        ) {
            duelSettings.build = !duelSettings.build
            reload()
        }

        setSlot(
            15, ItemStacks.TOGGLE_DAMAGE
                .lore(
                    (CC.WHITE + "Changes if you can " + CC.SECONDARY + "lose health" + CC.WHITE
                            + "."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + duelSettings.damage,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle damage."
                )
                .build()
        ) {
            duelSettings.damage = !duelSettings.damage
            reload()
        }

        setSlot(
            16, ItemStacks.TOGGLE_GRIEFING
                .lore(
                    (CC.WHITE + "Changes if you can " + CC.SECONDARY + "break the map" + CC.WHITE
                            + "."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + duelSettings.griefing,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle griefing."
                )
                .build()
        ) {
            duelSettings.griefing = !duelSettings.griefing
            reload()
        }

        setSlot(
            19, ItemStacks.PEARL_COOLDOWN
                .lore(
                    (CC.WHITE + "Changes how frequently you can " + CC.SECONDARY + "throw a pearl" + CC.WHITE
                            + "."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + duelSettings.pearlCooldown + " Seconds",
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change cooldown."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile
                .openMenu(
                    ConfigureValueMenu.of(
                        this,
                        { duelSettings.pearlCooldown = it },
                        Int::class
                    )
                )
        }

        val arenaCount =
            duelSettings.enabledArenas.byte2BooleanEntrySet().stream().filter { it.booleanValue }.count().toInt()
        setSlot(
            20, ItemStacks.ARENA
                .lore(
                    (CC.WHITE + "Changes the " + CC.SECONDARY + "arenas" + CC.WHITE
                            + "."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + arenaCount + " Enabled Arenas",
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change arena."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.openMenu(
                QueueArenaEnableMenu(
                    arenas.keys,
                    { this.reload() },
                    this
                )
            )
        }

        setSlot(
            21, ItemStacks.DEADLY_WATER
                .lore(
                    (CC.WHITE + "Changes if " + CC.SECONDARY + "water can kill you" + CC.WHITE
                            + "."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + duelSettings.deadlyWater,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle deadly water."
                )
                .build()
        ) {
            duelSettings.deadlyWater = !duelSettings.deadlyWater
            reload()
        }

        setSlot(
            22, ItemStacks.REGENERATION
                .lore(
                    (CC.WHITE + "Changes if you " + CC.SECONDARY + "regenerate" + CC.WHITE
                            + " health."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + duelSettings.regeneration,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle regeneration."
                )
                .build()
        ) {
            duelSettings.regeneration = !duelSettings.regeneration
            reload()
        }

        setSlot(
            23, ItemStacks.BOXING
                .lore(
                    (CC.WHITE + "Changes if you die after " + CC.SECONDARY + "100 hits" + CC.WHITE
                            + "."), " ",
                    CC.WHITE + "Currently:", CC.GOLD + duelSettings.boxing,
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle boxing."
                )
                .build()
        ) {
            duelSettings.boxing = !duelSettings.boxing
            reload()
        }

        val slot = addOnNextRow(15, ItemStacks.RESET_SETTINGS) {
            viewer.resetDuelSettings()
            reload()
        }

        addOnRow(slot, 4, ItemStacks.SUBMIT) {
            viewer.player.closeInventory()
            submitAction.execute(viewer)
        }

        prevMenu?.let {
            addOnRow(slot, 2, ItemStacks.BACK) { interaction: Interaction ->
                interaction.profile.openMenu(it)
            }
        }
    }

    override val title: String
        get() = CC.BLUE + "Game Mechanics"

    override fun shouldUpdate() = true
}
