package gg.mineral.practice.inventory.menus

import gg.mineral.practice.PracticePlugin
import gg.mineral.practice.entity.appender.PlayerAppender
import gg.mineral.practice.inventory.ClickCancelled
import gg.mineral.practice.inventory.Interaction
import gg.mineral.practice.inventory.PracticeMenu
import gg.mineral.practice.util.CoreConnector
import gg.mineral.practice.util.items.ItemStacks
import gg.mineral.practice.util.messages.CC
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@ClickCancelled(true)
class SettingsMenu : PracticeMenu(), PlayerAppender {
    override val title: String
        get() = CC.BLUE + "Settings"

    override fun update() {
        setSlot(
            10,
            ItemStacks.TOGGLE_DUEL_REQUESTS
                .lore(
                    CC.WHITE + "Toggles " + CC.SECONDARY + "duel requests" + CC.WHITE + ".",
                    " ",
                    CC.WHITE + "Currently:",
                    if (viewer.duelRequests)
                        CC.GREEN + "Enabled"
                    else
                        CC.RED + "Disabled",
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.player?.performCommand("toggleduelrequests")
            reload()
        }
        setSlot(
            12,
            ItemStacks.TOGGLE_PARTY_REQUESTS
                .lore(
                    CC.WHITE + "Toggles " + CC.SECONDARY + "party requests" + CC.WHITE + ".",
                    " ",
                    CC.WHITE + "Currently:",
                    if (viewer.partyRequests)
                        CC.GREEN + "Enabled"
                    else
                        CC.RED + "Disabled",
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.player?.performCommand("togglepartyrequests")
            reload()
        }
        setSlot(
            14,
            ItemStacks.TOGGLE_SCOREBOARD
                .lore(
                    CC.WHITE + "Toggles the" + CC.SECONDARY + " scoreboard" + CC.WHITE + ".",
                    " ",
                    CC.WHITE + "Currently:",
                    if (viewer.scoreboardEnabled)
                        CC.GREEN + "Enabled"
                    else
                        CC.RED + "Disabled",
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            interaction.profile.player?.performCommand("togglescoreboard")
            reload()
        }

        setSlot(
            16,
            ItemStacks.CHANGE_TIME.lore(
                CC.WHITE + "Changes the" + CC.SECONDARY + " time" + CC.WHITE + ".",
                " ",
                CC.WHITE + "Currently:",
                if (viewer.player?.isNight() == true)
                    CC.PURPLE + "Night"
                else
                    CC.GOLD + "Day",
                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
            )
                .build()
        ) {
            it.profile.player?.performCommand(if (viewer.player?.isNight() == true) "day" else "night")
            Bukkit.getScheduler().runTaskLater(PracticePlugin.INSTANCE, { reload() }, 2)
        }
        setSlot(
            28,
            ItemStacks.TOGGLE_PLAYER_VISIBILITY
                .lore(
                    CC.WHITE + "Toggles " + CC.SECONDARY + "visibility in the lobby" + CC.WHITE + ".",
                    " ",
                    CC.WHITE + "Currently:",
                    if (viewer.playersVisible)
                        CC.GREEN + "Enabled"
                    else
                        CC.RED + "Disabled",
                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                )
                .build()
        ) { interaction: Interaction ->
            val p = interaction.profile
            p.player?.performCommand("toggleplayervisibility")
            reload()
        }

        if (CoreConnector.connected()) {
            CoreConnector.INSTANCE?.settingsSQL?.getSettingsData(viewer.uuid)

            val privateMessages = CoreConnector.INSTANCE?.settingsSQL?.settingsMsg
            val privateMessagesSound = CoreConnector.INSTANCE?.settingsSQL?.settingsPmSound
            val globalChat = CoreConnector.INSTANCE?.settingsSQL?.settingsGlobalChat

            setSlot(
                30,
                ItemStacks.TOGGLE_PRIVATE_MESSAGES
                    .lore(
                        CC.WHITE + "Toggles " + CC.SECONDARY + "private messages" + CC.WHITE +
                                ".",
                        " ",
                        CC.WHITE + "Currently:",
                        if (privateMessages == true)
                            CC.GREEN + "Enabled"
                        else
                            CC.RED + "Disabled",
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                    )
                    .build()
            ) { interaction: Interaction ->
                val p = interaction.profile
                if (privateMessages == true) CoreConnector.INSTANCE?.settingsSQL?.disableMsg(p.player)
                else CoreConnector.INSTANCE?.settingsSQL?.enableMsg(p.player)
                reload()
            }

            setSlot(
                32,
                ItemStacks.TOGGLE_PRIVATE_MESSAGES_SOUNDS
                    .lore(
                        CC.WHITE + "Toggles " + CC.SECONDARY + "private message sounds" +
                                CC.WHITE + ".",
                        " ",
                        CC.WHITE + "Currently:",
                        if (privateMessagesSound == true)
                            CC.GREEN + "Enabled"
                        else
                            CC.RED + "Disabled",
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                    )
                    .build()
            ) { interaction: Interaction ->
                val p = interaction.profile
                if (privateMessagesSound == true) CoreConnector.INSTANCE?.settingsSQL?.disablePmSound(p.player)
                else CoreConnector.INSTANCE?.settingsSQL?.enablePmSound(p.player)
                reload()
            }

            setSlot(
                34,
                ItemStacks.TOGGLE_GLOBAL_CHAT
                    .lore(
                        CC.WHITE + "Toggles " + CC.SECONDARY + "Global Chat" + CC.WHITE + ".",
                        " ",
                        CC.WHITE + "Currently:",
                        if (globalChat == true)
                            CC.GREEN + "Enabled"
                        else
                            CC.RED + "Disabled",
                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value."
                    )
                    .build()
            ) { interaction: Interaction ->
                val p = interaction.profile
                if (globalChat == true) CoreConnector.INSTANCE?.settingsSQL?.disableGlobalChat(p.player)
                else CoreConnector.INSTANCE?.settingsSQL?.enableGlobalChat(p.player)
                reload()
            }

            setSlot(36, ItemStack(Material.AIR))
        }
    }

    override fun shouldUpdate() = true
}
