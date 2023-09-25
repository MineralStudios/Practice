package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.CoreConnector;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ChatMessages;

public class SettingsMenu extends PracticeMenu {

	public SettingsMenu() {
		super(CC.BLUE + "Settings");
		setClickCancelled(true);
	}

	@Override
	public boolean update() {
		setSlot(4, ItemStacks.SETTINGS);

		/*
		 * setSlot(0,
		 * ItemStacks.TOGGLE_PLAYER_VISIBILITY,
		 * interaction -> {
		 * Profile p = interaction.getProfile();
		 * p.getPlayer().performCommand("toggleplayervisibility");
		 * });
		 */
		setSlot(18,
				ItemStacks.TOGGLE_DUEL_REQUESTS
						.lore(CC.WHITE + "Toggles " + CC.SECONDARY + "duel requests" + CC.WHITE + ".",
								" ",
								CC.WHITE + "Currently:",
								viewer.getRequestHandler().isDuelRequests() ? CC.GREEN + "Enabled"
										: CC.RED + "Disabled",
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
						.build(),
				interaction -> {
					Profile p = interaction.getProfile();
					p.getPlayer().performCommand("toggleduelrequests");
					reload();
				});
		setSlot(19,
				ItemStacks.TOGGLE_PARTY_REQUESTS
						.lore(CC.WHITE + "Toggles " + CC.SECONDARY + "party requests" + CC.WHITE + ".",
								" ",
								CC.WHITE + "Currently:",
								viewer.getRequestHandler().isPartyRequests() ? CC.GREEN + "Enabled"
										: CC.RED + "Disabled",
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
						.build(),
				interaction -> {
					Profile p = interaction.getProfile();
					p.getPlayer().performCommand("togglepartyrequests");
					reload();
				});
		setSlot(20,
				ItemStacks.TOGGLE_SCOREBOARD
						.lore(CC.WHITE + "Toggles the" + CC.SECONDARY + " scoreboard" + CC.WHITE + ".",
								" ",
								CC.WHITE + "Currently:",
								viewer.isScoreboardEnabled() ? CC.GREEN + "Enabled"
										: CC.RED + "Disabled",
								CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
						.build(),
				interaction -> {
					Profile p = interaction.getProfile();
					p.getPlayer().performCommand("togglescoreboard");
					reload();
				});

		setSlot(21,
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

		if (CoreConnector.connected()) {
			CoreConnector.INSTANCE.getSettingsSQL().getSettingsData(viewer.getUuid());

			boolean friendsSound = CoreConnector.INSTANCE.getSettingsSQL().settingsFriendsSound;
			boolean privateMessages = CoreConnector.INSTANCE.getSettingsSQL().settingsMsg;
			boolean friendRequests = CoreConnector.INSTANCE.getSettingsSQL().friendsRequests;
			boolean privateMessagesSound = CoreConnector.INSTANCE.getSettingsSQL().settingsPmSound;
			boolean globalChat = CoreConnector.INSTANCE.getSettingsSQL().settingsGlobalChat;

			setSlot(22,
					ItemStacks.TOGGLE_PRIVATE_MESSAGES
							.lore(CC.WHITE + "Toggles " + CC.SECONDARY + "private messages" + CC.WHITE + ".",
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

						p.message(ChatMessages.PRIVATE_MESSAGES_TOGGLED.clone()
								.replace("%toggled%", privateMessages ? "disabled" : "enabled"));
						reload();
					});

			setSlot(23,
					ItemStacks.TOGGLE_FRIENDS_SOUNDS
							.lore(CC.WHITE + "Toggles " + CC.SECONDARY + "friend sounds" + CC.WHITE + ".",
									" ",
									CC.WHITE + "Currently:",
									friendsSound ? CC.GREEN + "Enabled"
											: CC.RED + "Disabled",
									CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
							.build(),
					interaction -> {
						Profile p = interaction.getProfile();
						if (friendsSound)
							CoreConnector.INSTANCE.getSettingsSQL().disableFriendsSound(p.getPlayer());
						else
							CoreConnector.INSTANCE.getSettingsSQL().enableFriendsSound(p.getPlayer());

						p.message(ChatMessages.FRIEND_SOUND_TOGGLED.clone()
								.replace("%toggled%", friendsSound ? "disabled" : "enabled"));
						reload();
					});

			setSlot(24,
					ItemStacks.TOGGLE_FRIEND_REQUESTS
							.lore(CC.WHITE + "Toggles " + CC.SECONDARY + "friend requests" + CC.WHITE + ".",
									" ",
									CC.WHITE + "Currently:",
									friendRequests ? CC.GREEN + "Enabled"
											: CC.RED + "Disabled",
									CC.BOARD_SEPARATOR, CC.ACCENT + "Click to change value.")
							.build(),
					interaction -> {
						Profile p = interaction.getProfile();
						p.getPlayer().performCommand(friendRequests ? "friends disable" : "friends enable");
						reload();
					});

			setSlot(25,
					ItemStacks.TOGGLE_PRIVATE_MESSAGES_SOUNDS
							.lore(CC.WHITE + "Toggles " + CC.SECONDARY + "private message sounds" + CC.WHITE + ".",
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

						p.message(ChatMessages.PRIVATE_MESSAGE_SOUNDS_TOGGLED.clone()
								.replace("%toggled%", privateMessagesSound ? "disabled" : "enabled"));
						reload();
					});

			setSlot(26,
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

						p.message(ChatMessages.GLOBAL_CHAT_TOGGLED.clone()
								.replace("%toggled%", globalChat ? "disabled" : "enabled"));
						reload();
					});
		}

		return true;
	}
}
