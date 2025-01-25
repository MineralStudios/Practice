package gg.mineral.practice.inventory.menus

import gg.mineral.api.collection.GlueList
import gg.mineral.practice.bots.Difficulty
import gg.mineral.practice.category.Category
import gg.mineral.practice.entity.PlayerStatus
import gg.mineral.practice.gametype.Gametype
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.Menu
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.managers.GametypeManager
import gg.mineral.practice.managers.MatchManager.getInGameCount
import gg.mineral.practice.queue.QueueSettings
import gg.mineral.practice.queue.QueueSettings.BotTeamSetting
import gg.mineral.practice.queue.QueueSystem
import gg.mineral.practice.queue.Queuetype
import gg.mineral.practice.queue.QueuetypeMenuEntry
import gg.mineral.practice.util.items.ItemBuilder
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.impl.ErrorMessages
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap
import org.bukkit.event.inventory.ClickType
import java.util.function.Consumer

@ClickCancelled(true)
open class SelectGametypeMenu(
    protected var queuetype: Queuetype,
    protected var type: Type,
    protected var prevMenu: Menu? = null
) :
    PracticeMenu() {

    protected val menuEntries: Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> by lazy { setMenuEntries() }

    protected open fun setMenuEntries(): Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> = queuetype.menuEntries

    fun queue(gametype: Gametype, interact: Interaction) {
        val viewer = interact.profile
        val queueEntry = QueueSystem.getQueueEntry(viewer, queuetype, gametype)

        if (queueEntry != null) {
            viewer.removeFromQueue(queuetype, gametype)
            return
        }

        val queueInteraction =
            Consumer { _: Interaction ->
                viewer.addPlayerToQueue(queuetype, gametype)
                if (viewer.openMenu !is SelectGametypeMenu && viewer.playerStatus === PlayerStatus.QUEUEING) viewer.openMenu(
                    SelectGametypeMenu(
                        queuetype, type,
                        this.prevMenu
                    )
                )
            }

        val queueSettings = viewer.queueSettings
        val botQueue = queuetype.botsEnabled && type == Type.UNRANKED && queueSettings.botQueue
        if (botQueue && !gametype.botsEnabled) {
            viewer.message(ErrorMessages.COMING_SOON)
            return
        }

        if (viewer.queueSettings.arenaSelection) viewer.openMenu(
            QueueArenaEnableMenu(
                queuetype.filterArenasByGametype(gametype),
                queueInteraction, this
            )
        )
        else queueInteraction.accept(interact)
    }

    private fun addSurroundingButtons(queueSettings: QueueSettings, botQueue: Boolean) {
        if (type != Type.UNRANKED && type != Type.QUEUE) return

        val oldCombat = queueSettings.oldCombat

        setSlot(
            if (type == Type.UNRANKED) 49 else 40,
            ItemStacks.OLD_COMBAT.name(CC.SECONDARY + CC.B + "Old Combat Mechanics").lore(
                (CC.WHITE + "Play using " + CC.SECONDARY + "old combat" + CC.WHITE
                        + " seen on servers from 2015-2017."),
                " ",
                CC.WHITE + "Currently:", if (oldCombat) CC.GREEN + "Enabled" else CC.RED + "Disabled", " ",
                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle old combat."
            ).build()
        ) { interaction: Interaction ->
            interaction.profile.queueSettings.oldCombat = !oldCombat
            reload()
        }

        if (type != Type.UNRANKED) return

        val partySize = viewer.party?.partyMembers?.size ?: 1

        if (queueSettings.teamSize < partySize) queueSettings.teamSize = (viewer.party!!.partyMembers.size).toByte()

        val teamSize = queueSettings.teamSize
        setSlot(
            if (botQueue) 2 else 3,
            ItemStacks.TEAMFIGHT.lore(
                CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "team" + CC.WHITE + " match.",
                " ", CC.WHITE + "Currently:", teamSizeColors[teamSize.toInt()], " ", CC.BOARD_SEPARATOR,
                if (teamSize > teamSizeColors.size)
                    CC.RED + "Maximum team size reached."
                else
                    CC.ACCENT + "Click to change team size."
            )
                .build(),
            TEAM_SIZE_INTERACTION
        )

        val opponentDifficulty = queueSettings.opponentDifficulty
        val teamDifficulty = queueSettings.teammateDifficulty

        var item = ItemStacks.BOT_QUEUE_DISABLED
        if (botQueue) {
            if (queueSettings.teamSize.toInt() == 1) setSlot(
                4,
                ItemStacks.BOT_SETTINGS.lore(
                    (CC.WHITE + "Allows you to configure the " + CC.SECONDARY + "difficulty" + CC.WHITE
                            + "."),
                    " ", CC.WHITE + "Selected Difficulty: ",
                    Difficulty.entries[opponentDifficulty.toInt()].display, " ",
                    CC.BOARD_SEPARATOR, " ", CC.GREEN + "Left Click to change difficulty.",
                    CC.RED + "Right Click to configure custom difficulty."
                ).build(),
                DIFFICULTY_INTERACTION
            )
            else if (queueSettings.botTeamSetting == BotTeamSetting.BOTH && teamSize > partySize) setSlot(
                4,
                ItemStacks.BOT_SETTINGS.lore(
                    (CC.WHITE + "Allows you to configure the " + CC.SECONDARY + "difficulty" + CC.WHITE
                            + "."),
                    " ",
                    CC.WHITE + "Opponent Difficulty: ",
                    Difficulty.entries[opponentDifficulty.toInt()].display, " ",
                    CC.WHITE + "Team Difficulty: ",
                    Difficulty.entries[teamDifficulty.toInt()].display, " ",
                    CC.BOARD_SEPARATOR, " ", CC.GREEN + "Left Click to change opponent difficulty.",
                    CC.RED + "Right Click to change team difficulty."
                ).build(),
                DIFFICULTY_INTERACTION
            )
            else setSlot(
                4,
                ItemStacks.BOT_SETTINGS.lore(
                    (CC.WHITE + "Allows you to configure the " + CC.SECONDARY + "difficulty" + CC.WHITE
                            + "."),
                    " ",
                    CC.WHITE + "Opponent Difficulty: ",
                    Difficulty.entries[opponentDifficulty.toInt()].display, " ",
                    CC.BOARD_SEPARATOR, " ", CC.GREEN + "Left Click to change opponent difficulty."
                )
                    .build(),
                DIFFICULTY_INTERACTION
            )

            item = if (teamSize > partySize) {
                when (queueSettings.botTeamSetting) {
                    BotTeamSetting.BOTH -> {
                        ItemStacks.BOT_QUEUE_ENABLED_TEAM.lore(
                            (CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "team bot" + CC.WHITE
                                    + " match "),
                            CC.WHITE + "with a bot teammate and bot opponents.",
                            " ",
                            CC.WHITE + "Currently:",
                            CC.GREEN + "Enabled",
                            " ", CC.WHITE + "Team Settings:", CC.PINK + "Bot Teammate and Opponents", " ",
                            CC.BOARD_SEPARATOR,
                            CC.GREEN + "Left click to toggle bots.",
                            CC.RED + "Right click to change team settings."
                        )
                            .build()
                    }

                    BotTeamSetting.OPPONENT -> {
                        ItemStacks.BOT_QUEUE_ENABLED_TEAM.lore(
                            (CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "team bot" + CC.WHITE
                                    + " match "),
                            CC.WHITE + "with a bots as your opponents.",
                            " ",
                            CC.WHITE + "Currently:",
                            CC.GREEN + "Enabled",
                            " ", CC.WHITE + "Team Settings:", CC.AQUA + "Bot Opponents", " ",
                            CC.BOARD_SEPARATOR,
                            CC.GREEN + "Left click to toggle bots.",
                            CC.RED + "Right click to change team settings."
                        )
                            .build()
                    }
                }
            } else ItemStacks.BOT_QUEUE_ENABLED
        }

        setSlot(if (botQueue) 6 else 5, item, BOT_QUEUE_INTERACTION)

        setSlot(47, ItemStacks.RANDOM_QUEUE) { interaction: Interaction ->
            val gametype = if (viewer.queueSettings.botQueue)
                queuetype.randomGametypeWithBotsEnabled()
            else
                queuetype.randomGametype()
            queue(gametype, interaction)
            if (viewer.openMenu is SelectGametypeMenu) viewer.player.closeInventory()
        }

        val arenaSelection = viewer.queueSettings.arenaSelection

        setSlot(
            51,
            ItemStacks.ARENA.lore(
                CC.WHITE + "Select an " + CC.SECONDARY + "arena" + CC.WHITE + " when you queue.", " ",
                CC.WHITE + "Currently:", if (arenaSelection) CC.GREEN + "Enabled" else CC.RED + "Disabled", " ",
                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle arena selection."
            ).build(),
            ARENA_INTERACTION
        )
    }

    protected open fun shouldSkip(menuEntry: QueuetypeMenuEntry): Boolean {
        return menuEntry is Gametype && menuEntry.inCategory
    }

    override fun update() {
        clear()
        val queueSettings = viewer.queueSettings

        val botQueue = queuetype.botsEnabled && type == Type.UNRANKED && queueSettings.botQueue

        addSurroundingButtons(queueSettings, botQueue)

        if (prevMenu != null) setSlot(
            if (type == Type.UNRANKED) 45 else 40,
            ItemStacks.BACK
        ) { interaction: Interaction? ->
            viewer.openMenu(
                prevMenu!!
            )
        }

        var offset = 0

        for (entry in menuEntries
            .object2IntEntrySet()) {
            val menuEntry = entry.key

            if (shouldSkip(menuEntry)) continue

            if (botQueue && !menuEntry.botsEnabled) {
                offset++
                continue
            }

            val itemBuild = ItemBuilder(menuEntry.displayItem.clone())
                .name(CC.SECONDARY + CC.B + menuEntry.displayName)

            if (menuEntry is Gametype) {
                val teamSize = queueSettings.teamSize
                val queueEntry = QueueSettings.toEntry(
                    queuetype, menuEntry, teamSize.toInt(), queueSettings.botQueue,
                    queueSettings.oldCombat,
                    queueSettings.opponentDifficulty,
                    queueSettings.botTeamSetting,
                    queueSettings.enabledArenas
                )

                if (type == Type.QUEUE || type == Type.UNRANKED) {
                    if (QueueSystem.getQueueEntry(
                            viewer,
                            queuetype,
                            menuEntry
                        ) != null
                    ) itemBuild.lore(CC.RED + "Click to leave queue.")
                    else itemBuild.lore(
                        (CC.SECONDARY + "In Queue: " + CC.WHITE
                                + QueueSystem.getCompatibleQueueCount(
                            queueEntry.queuetype,
                            queueEntry.gametype
                        )),
                        (CC.SECONDARY + "In Game: " + CC.WHITE
                                + getInGameCount(queuetype, menuEntry)),
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to queue."
                    )
                } else itemBuild.lore()

                val item = itemBuild.build()

                setSlot(
                    entry.intValue + (if (type == Type.UNRANKED) 18 else 9) - offset, item
                ) { interaction: Interaction ->
                    if (type == Type.KIT_EDITOR) {
                        viewer.player.closeInventory()
                        viewer.sendToKitEditor(queuetype, menuEntry)
                        return@setSlot
                    }
                    queue(menuEntry, interaction)

                    val menu = viewer.openMenu
                    if (menu != null && menu == this && viewer.playerStatus === PlayerStatus.QUEUEING) reload()
                }
                continue
            }

            if (menuEntry is Category) {
                val sb = GlueList<String>()
                sb.add(CC.SECONDARY + "Includes:")

                menuEntry.gametypes.mapNotNull { GametypeManager.gametypes[it] }
                    .forEach {
                        val isQueued = QueueSystem.getQueueEntry(viewer, queuetype, it) != null
                        sb.add(CC.WHITE + it.displayName + (if (isQueued) " - " + CC.GREEN + "Queued" else ""))
                    }

                sb.add(" ")
                sb.add(CC.BOARD_SEPARATOR)
                sb.add(CC.ACCENT + "Click to view category.")

                itemBuild.lore(*sb.toTypedArray<String>())
                val item = itemBuild.build()
                setSlot(
                    entry.intValue + (if (type == Type.UNRANKED) 18 else 9) - offset, item
                ) { interaction: Interaction ->
                    interaction.profile
                        .openMenu(SelectCategorizedGametypeMenu(queuetype, menuEntry, type, this))
                }
            }
        }
    }

    override val title: String
        get() = CC.BLUE + queuetype.displayName

    override fun shouldUpdate() = true

    enum class Type {
        QUEUE, KIT_EDITOR, UNRANKED
    }

    companion object {
        protected val teamSizeColors: Int2ObjectOpenHashMap<String> = object : Int2ObjectOpenHashMap<String>() {
            init {
                put(1, CC.RED + "1v1")
                put(2, CC.GREEN + "2v2")
                put(3, CC.AQUA + "3v3")
                put(4, CC.YELLOW + "4v4")
                put(5, CC.PINK + "5v5")
                put(6, CC.GOLD + "6v6")
                put(7, CC.BLUE + "7v7")
                put(8, CC.PURPLE + "8v8")
            }

            override fun getOrDefault(k: Int, defaultValue: String?) = super.getOrDefault(k, defaultValue)

            override fun containsKey(k: Int) = super.containsKey(k)

            override fun get(k: Int) = super.get(k)

            override fun remove(k: Int) = super.remove(k)
        }

        protected val TEAM_SIZE_INTERACTION: Consumer<Interaction> =
            Consumer { interaction: Interaction ->
                val viewer = interaction.profile
                val queueSettings = viewer.queueSettings
                queueSettings.teamSize = (queueSettings.teamSize % 8 + 1).toByte()

                val menu = viewer.openMenu
                menu?.reload()
            }
        protected val DIFFICULTY_INTERACTION: Consumer<Interaction> =
            Consumer<Interaction> { interaction: Interaction ->
                val p = interaction.profile
                val queueSettings = p.queueSettings

                val menu = p.openMenu

                if (interaction.clickType == ClickType.LEFT) {
                    queueSettings.opponentDifficulty = ((queueSettings.opponentDifficulty + 1)
                            % Difficulty.entries.size).toByte()

                    val difficulty = queueSettings.opponentBotDifficulty

                    if (difficulty === Difficulty.CUSTOM) queueSettings.opponentDifficulty =
                        ((queueSettings.opponentDifficulty + 1)
                                % Difficulty.entries.size).toByte()
                } else if (interaction.clickType == ClickType.RIGHT) {
                    if (queueSettings.teamSize <= 1) {
                        if (p.player.hasPermission("practice.custombot")
                            && menu is SelectGametypeMenu
                        ) p.openMenu(CustomBotDifficultyMenu(menu))
                        else ErrorMessages.RANK_REQUIRED.send(p.player)
                        return@Consumer
                    }

                    val teamSize = queueSettings.teamSize
                    val partySize = p.party?.partyMembers?.size ?: 1

                    if (queueSettings.botTeamSetting == BotTeamSetting.BOTH && teamSize <= partySize) return@Consumer

                    queueSettings.teammateDifficulty = ((queueSettings.teammateDifficulty + 1)
                            % Difficulty.entries.size).toByte()

                    val difficulty = queueSettings.teammateBotDifficulty

                    if (difficulty === Difficulty.CUSTOM) queueSettings.teammateDifficulty =
                        ((queueSettings.teammateDifficulty + 1)
                                % Difficulty.entries.size).toByte()
                }
                menu?.reload()
            }
        protected val BOT_QUEUE_INTERACTION: Consumer<Interaction> =
            Consumer<Interaction> { interaction: Interaction ->
                val viewer = interaction.profile
                val queueSettings = viewer.queueSettings
                val teamSize = queueSettings.teamSize
                val partySize = viewer.party?.partyMembers?.size ?: 1
                if (interaction.clickType == ClickType.LEFT) queueSettings.botQueue = !queueSettings.botQueue
                else if (viewer.queueSettings.botQueue && interaction.clickType == ClickType.RIGHT && teamSize > partySize) queueSettings.botTeamSetting =
                    BotTeamSetting.entries[(queueSettings.botTeamSetting.ordinal
                            + 1) % BotTeamSetting.entries.size]

                val menu = viewer.openMenu
                val botQueue = queueSettings.botQueue
                if (menu != null) if (menu is SelectCategorizedGametypeMenu && botQueue
                    && !menu.category.botsEnabled
                ) viewer.openMenu(
                    SelectGametypeMenu(
                        menu.queuetype,
                        menu.type, menu.prevMenu
                    )
                )
                else menu.reload()
            }
        protected val ARENA_INTERACTION: Consumer<Interaction> =
            Consumer { interaction: Interaction ->
                val viewer = interaction.profile
                val queueSettings = viewer.queueSettings
                queueSettings.arenaSelection = !queueSettings.arenaSelection

                val menu = viewer.openMenu
                menu?.reload()
            }
    }
}
