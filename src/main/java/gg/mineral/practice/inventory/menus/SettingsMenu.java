package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.CoreConnector;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@ClickCancelled(true)
public class SettingsMenu extends PracticeMenu {

    @Override
    public void update() {
        setSlot(10,
                ItemStacks.TOGGLE_DUEL_REQUESTS
                        .lore(CC.WHITE + "Toggles " + CC.SECONDARY + "duel requests" + CC.WHITE + ".",
                                " ",
                                CC.WHITE + "Currently:",
                                viewer.getRequestHandler().isDuelRequests() ? CC.GREEN + "Enabled"
                                        : CC.RED + "Disabled",
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> {
                    interaction.getProfile().getPlayer().performCommand("toggleduelrequests");
                    reload();
                });
        setSlot(12,
                ItemStacks.TOGGLE_PARTY_REQUESTS
                        .lore(CC.WHITE + "Toggles " + CC.SECONDARY + "party requests" + CC.WHITE + ".",
                                " ",
                                CC.WHITE + "Currently:",
                                viewer.getRequestHandler().isPartyRequests() ? CC.GREEN + "Enabled"
                                        : CC.RED + "Disabled",
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> {
                    interaction.getProfile().getPlayer().performCommand("togglepartyrequests");
                    reload();
                });
        setSlot(14,
                ItemStacks.TOGGLE_SCOREBOARD
                        .lore(CC.WHITE + "Toggles the" + CC.SECONDARY + " scoreboard" + CC.WHITE + ".",
                                " ",
                                CC.WHITE + "Currently:",
                                viewer.isScoreboardEnabled() ? CC.GREEN + "Enabled"
                                        : CC.RED + "Disabled",
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> {
                    interaction.getProfile().getPlayer().performCommand("togglescoreboard");
                    reload();
                });

        setSlot(16,
                ItemStacks.CHANGE_TIME.lore(CC.WHITE + "Changes the" + CC.SECONDARY + " time" + CC.WHITE + ".",
                                " ",
                                CC.WHITE + "Currently:",
                                viewer.isNightMode() ? CC.PURPLE + "Night"
                                        : CC.GOLD + "Day",
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> {
                    Profile p = interaction.getProfile();
                    p.getPlayer().performCommand(p.isNightMode() ? "day" : "night");
                    reload();
                });
        setSlot(28,
                ItemStacks.TOGGLE_PLAYER_VISIBILITY
                        .lore(CC.WHITE + "Toggles " + CC.SECONDARY + "visibility in the lobby" + CC.WHITE + ".",
                                " ",
                                CC.WHITE + "Currently:",
                                viewer.isPlayersVisible() ? CC.GREEN + "Enabled"
                                        : CC.RED + "Disabled",
                                CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                        .build(),
                interaction -> {
                    Profile p = interaction.getProfile();
                    p.getPlayer().performCommand("toggleplayervisibility");
                    reload();
                });

        if (CoreConnector.connected()) {
            CoreConnector.INSTANCE.getSettingsSQL().getSettingsData(viewer.getUuid());

            boolean privateMessages = CoreConnector.INSTANCE.getSettingsSQL().settingsMsg;
            boolean privateMessagesSound = CoreConnector.INSTANCE.getSettingsSQL().settingsPmSound;
            boolean globalChat = CoreConnector.INSTANCE.getSettingsSQL().settingsGlobalChat;

            setSlot(30,
                    ItemStacks.TOGGLE_PRIVATE_MESSAGES
                            .lore(CC.WHITE + "Toggles " + CC.SECONDARY + "private messages" + CC.WHITE +
                                            ".",
                                    " ",
                                    CC.WHITE + "Currently:",
                                    privateMessages ? CC.GREEN + "Enabled"
                                            : CC.RED + "Disabled",
                                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                            .build(),
                    interaction -> {
                        Profile p = interaction.getProfile();

                        if (privateMessages)
                            CoreConnector.INSTANCE.getSettingsSQL().disableMsg(p.getPlayer());
                        else
                            CoreConnector.INSTANCE.getSettingsSQL().enableMsg(p.getPlayer());

                        reload();
                    });

            setSlot(32,
                    ItemStacks.TOGGLE_PRIVATE_MESSAGES_SOUNDS
                            .lore(CC.WHITE + "Toggles " + CC.SECONDARY + "private message sounds" +
                                            CC.WHITE + ".",
                                    " ",
                                    CC.WHITE + "Currently:",
                                    privateMessagesSound ? CC.GREEN + "Enabled"
                                            : CC.RED + "Disabled",
                                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                            .build(),
                    interaction -> {
                        Profile p = interaction.getProfile();
                        if (privateMessagesSound)
                            CoreConnector.INSTANCE.getSettingsSQL().disablePmSound(p.getPlayer());
                        else
                            CoreConnector.INSTANCE.getSettingsSQL().enablePmSound(p.getPlayer());

                        reload();
                    });

            setSlot(34,
                    ItemStacks.TOGGLE_GLOBAL_CHAT
                            .lore(CC.WHITE + "Toggles " + CC.SECONDARY + "Global Chat" + CC.WHITE + ".",
                                    " ",
                                    CC.WHITE + "Currently:",
                                    globalChat ? CC.GREEN + "Enabled"
                                            : CC.RED + "Disabled",
                                    CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
                            .build(),
                    interaction -> {
                        Profile p = interaction.getProfile();
                        if (globalChat)
                            CoreConnector.INSTANCE.getSettingsSQL().disableGlobalChat(p.getPlayer());
                        else
                            CoreConnector.INSTANCE.getSettingsSQL().enableGlobalChat(p.getPlayer());

                        reload();
                    });

            setSlot(36, new ItemStack(Material.AIR));
        }

    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Settings";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
