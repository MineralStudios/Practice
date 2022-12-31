package gg.mineral.practice.util.messages.impl;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.ClickableChatMessage;
import gg.mineral.practice.util.messages.ListElementMessage;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class ChatMessages {
	// Value Set
	public static final ChatMessage ARENA_CREATED = new ChatMessage("The %arena% arena has been created.", CC.YELLOW)
			.highlightText(CC.GOLD, "%arena%"),
			ARENA_SPAWN_SET = new ChatMessage(
					"The spawn location for the %arena% arena has been set to your location.", CC.YELLOW)
					.highlightText(CC.GOLD, "%arena%", "your location"),
			ARENA_DISPLAY_SET = new ChatMessage(
					"The display item for the %arena% arena has been set to the item in your hand.", CC.YELLOW)
					.highlightText(CC.GOLD, "%arena%", "the item in your hand"),
			ARENA_DELETED = new ChatMessage("The %arena% arena has been deleted.", CC.YELLOW)
					.highlightText(CC.GOLD, "%arena%"),
			CATAGORY_CREATED = new ChatMessage("The %catagory% catagory has been created.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%"),
			CATAGORY_DISPLAY_SET = new ChatMessage(
					"The display item for the %catagory% catagory has been set to the item in your hand.", CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%", "the item in your hand"),
			CATAGORY_SLOT = new ChatMessage(
					"The slot in the queue for the %catagory% catagory has been set to %slot%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%", "%slot%"),
			CATAGORY_ADDED = new ChatMessage(
					"The %gametype% gametype has been added to the %catagory% catagory.", CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%", "%gametype%"),
			CATAGORY_REMOVED = new ChatMessage(
					"The %gametype% gametype has been removed from the %catagory% catagory.", CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%", "%gametype%"),
			CATAGORY_DELETED = new ChatMessage("The %catagory% catagory has been deleted.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%"),
			GAMETYPE_CREATED = new ChatMessage("The %gametype% gametype has been created.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%"),
			GAMETYPE_DISPLAY_SET = new ChatMessage(
					"The display item for the %gametype% gametype has been set to the item in your hand.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "the item in your hand"),
			GAMETYPE_DAMAGE_TICKS_SET = new ChatMessage(
					"The hit delay for the %gametype% gametype has been set to the %delay%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%delay%"),
			GAMETYPE_REGEN_SET = new ChatMessage(
					"Regeneration for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_GRIEFING_SET = new ChatMessage(
					"Griefing for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_BUILD_SET = new ChatMessage(
					"Build for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_DEADLY_WATER_SET = new ChatMessage(
					"Deadly water for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_LOOTING_SET = new ChatMessage(
					"Looting for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_DAMAGE_SET = new ChatMessage(
					"Damage for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_HUNGER_SET = new ChatMessage(
					"Hunger for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_BOXING_SET = new ChatMessage(
					"Boxing for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_EVENT_SET = new ChatMessage(
					"Event mode for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_LOADED_KIT = new ChatMessage(
					"You have been given the kit for the %gametype% gametype.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%"),
			GAMETYPE_KIT_SET = new ChatMessage(
					"The kit for the %gametype% gametype has been set to your inventory contents.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "your inventory contents"),
			GAMETYPE_SLOT_SET = new ChatMessage(
					"The slot for the %gametype% gametype has been set to %slot% for the %queuetype% queue.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%queuetype%", "%slot%"),
			GAMETYPE_PEARL_COOLDOWN_SET = new ChatMessage(
					"The pearl cooldown for the %gametype% gametype has been set to %cooldown%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%cooldown%"),
			GAMETYPE_ARENA_SET = new ChatMessage(
					"The %arena% arena has been set to %toggled% for the %gametype% gametype.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%", "%arena%"),
			GAMETYPE_EVENT_ARENA_SET = new ChatMessage(
					"The event arena for the %gametype% gametype has been set to the %arena% arena.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%arena%"),
			GAMETYPE_ARENA_FOR_ALL_SET = new ChatMessage(
					"The %arena% arena has been set to %toggled% for all gametypes.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%"),
			GAMETYPE_DELETED = new ChatMessage("The %gametype% gametype has been deleted.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%"),
			KIT_EDITOR_ENABLED = new ChatMessage("The kit editor has been set to %toggled%.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%toggled%"),
			KIT_EDITOR_DISPLAY_SET = new ChatMessage(
					"The kit editor display item has been set to the item in your hand.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "the item in your hand"),
			KIT_EDITOR_SLOT_SET = new ChatMessage(
					"The kit editor slot has been set to the %slot% slot.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%slot%"),
			KIT_EDITOR_LOCATION_SET = new ChatMessage(
					"The kit editor location has been set to your location.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "your location"),
			SPAWN_SET = new ChatMessage(
					"The spawn location for the server has been set to your location.", CC.YELLOW)
					.highlightText(CC.GOLD, "the server", "your location"),
			PARTIES_ENABLED = new ChatMessage("The parties feature has been set to %toggled%.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%toggled%"),
			PARTIES_DISPLAY_SET = new ChatMessage(
					"The parties display item has been set to the item in your hand.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "the item in your hand"),
			PARTIES_SLOT_SET = new ChatMessage(
					"The parties slot has been set to the %slot% slot.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%slot%"),
			QUEUETYPE_CREATED = new ChatMessage("The %queuetype% queuetype has been created.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%"),
			QUEUETYPE_DISPLAY_SET = new ChatMessage(
					"The display item for the %queuetype% queuetype has been set to the item in your hand.", CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "the item in your hand"),
			QUEUETYPE_RANKED_SET = new ChatMessage(
					"Event mode for the %queuetype% queuetype has been set to the %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "%toggled%"),
			QUEUETYPE_SLOT_SET = new ChatMessage(
					"The slot for the %queuetype% queuetype has been set to the %slot% slot.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "%slot%"),
			QUEUETYPE_KB_SET = new ChatMessage(
					"The knockback for the %queuetype% queuetype has been set to the %knockback% profile", CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "%knockback%"),
			QUEUETYPE_ARENA_SET = new ChatMessage(
					"The %arena% arena has been set to %toggled% for the %queuetype% queuetype.", CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "%toggled%", "%arena%"),
			QUEUETYPE_DELETED = new ChatMessage("The %queuetype% queuetype has been deleted.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%"),
			SETTINGS_ENABLED = new ChatMessage("The settings has been set to %toggled%.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%toggled%"),
			SETTINGS_DISPLAY_SET = new ChatMessage(
					"The settings display item has been set to the item in your hand.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "the item in your hand"),
			SETTINGS_SLOT_SET = new ChatMessage(
					"The settings slot has been set to the %slot% slot.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%slot%"),
			PARTY_CREATED = new ChatMessage("You have created a new party.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "party"),
			PARTY_OPENED = new ChatMessage(
					"Your party has been %opened%.",
					CC.YELLOW)
					.highlightText(CC.GOLD, "%opened%"),
			DUEL_REQUESTS_TOGGLED = new ChatMessage(
					"Your duel requests has now been %toggled%.",
					CC.YELLOW).highlightText(CC.GOLD, "%toggled%"),
			PARTY_REQUESTS_TOGGLED = new ChatMessage(
					"Your party requests has now been %toggled%.",
					CC.YELLOW).highlightText(CC.GOLD, "%toggled%"),
			VISIBILITY_TOGGLED = new ChatMessage(
					"Your player visibility has now been %toggled%.",
					CC.YELLOW).highlightText(CC.GOLD, "%toggled%"),
			STOP_SPECTATING = new ChatMessage(
					"Please type /stopspectating to stop spectating.",
					CC.YELLOW).highlightText(CC.GOLD, "/stopspectating"),
			KIT_SAVED = new ChatMessage(
					"Your kit has been saved.",
					CC.YELLOW);

	// Info
	public static final ChatMessage PEARL = new ChatMessage(
			"You can use the ender pearl again in %time% second(s).",
			CC.AQUA)
			.highlightText(CC.D_AQUA, "%time%"),
			JOINED_QUEUE = new ChatMessage(
					"You are now queued for %queue% %gametype%.",
					CC.AQUA)
					.highlightText(CC.D_AQUA, "%queue%", "%gametype%"),
			LEFT_QUEUE = new ChatMessage("You are no longer queued", CC.AQUA).highlightText(CC.D_AQUA, "%queue%",
					"%gametype%"),
			HEALTH = new ChatMessage("%player% now has %health% health remaining.", CC.AQUA).highlightText(CC.D_AQUA,
					"%health%", "%player%"),
			NO_OPPONENT = new ChatMessage(
					"There was no opponent available for this round. You will play in the next round instead.",
					CC.AQUA),
			SPECTATING_YOUR_MATCH = new ChatMessage("%player% is now spectating your match.", CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%"),
			SPECTATING_TOURNAMENT = new ChatMessage("You are now spectating the tournament.", CC.AQUA)
					.highlightText(CC.D_AQUA, "the tournament"),
			WON_TOURNAMENT = new ChatMessage("%player% has won the tournament.", CC.AQUA).highlightText(CC.D_AQUA,
					"%player%"),
			SPECTATING_EVENT = new ChatMessage("You are now spectating the event.", CC.AQUA).highlightText(CC.D_AQUA,
					"the event"),
			WON_EVENT = new ChatMessage("%player% has won the event.", CC.AQUA).highlightText(CC.D_AQUA, "%player%"),
			ROUND_OVER = new ChatMessage("Round %round% his over. The next round will start in 5 seconds.", CC.AQUA)
					.highlightText(CC.D_AQUA, "%round%"),
			BEGINS_IN = new ChatMessage("The match will begin in %time% second(s).", CC.AQUA)
					.highlightText(CC.D_AQUA, "%time%"),
			MATCH_STARTED = new ChatMessage("The match has started.", CC.D_AQUA, true),
			FOLLOWING = new ChatMessage("You are now following %player%.", CC.AQUA).highlightText(CC.D_AQUA,
					"%player%"),
			POTS = new ChatMessage("You have %pots% health potions in your inventory.", CC.AQUA)
					.highlightText(CC.D_AQUA, "%pots%"),
			DUEL_REQUEST_SENT = new ChatMessage(
					"You have send a duel request to %player%. They have 30 seconds to accept.", CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%"),
			SPECTATING = new ChatMessage("You are now spectating %player%.", CC.AQUA).highlightText(CC.D_AQUA,
					"%player%"),
			CAN_NOT_BROADCAST = new ChatMessage(
					"The message to join the party can only be broadcasted once every 20 seconds.", CC.AQUA),
			JOINED_PARTY = new ChatMessage("%player% has joined the party.", CC.GREEN).highlightText(CC.D_GREEN,
					"%player%"),
			LEFT_PARTY = new ChatMessage("%player% has left the party.", CC.RED).highlightText(CC.D_RED, "%player%"),
			JOINED_TOURNAMENT = new ChatMessage("%player% has joined the tourmament.", CC.GREEN)
					.highlightText(CC.D_GREEN, "%player%"),
			LEFT_TOURNAMENT = new ChatMessage("%player% has left the tournament.", CC.RED).highlightText(CC.D_RED,
					"%player%"),
			JOINED_EVENT = new ChatMessage("%player% has joined the event.", CC.GREEN).highlightText(CC.D_GREEN,
					"%player%"),
			LEFT_EVENT = new ChatMessage("%player% has left the event.", CC.RED).highlightText(CC.D_RED, "%player%"),
			PARTY_REQUEST_SENT = new ChatMessage(
					"You have send a party invite to %player%. They have 30 seconds to accept.", CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%");

	public static final ClickableChatMessage DUEL_REQUEST_RECIEVED = new ClickableChatMessage(
			"You have recieved a duel request from %player%. [Click To Accept]",
			CC.AQUA)
			.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Accept]"),
			BROADCAST_TOURNAMENT = new ClickableChatMessage(
					"%player% has started a tournament. [Click To Join]",
					CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Join]"),
			BROADCAST_EVENT = new ClickableChatMessage(
					"%player% has started an event. [Click To Join]",
					CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Join]"),
			PARTY_REQUEST_RECIEVED = new ClickableChatMessage(
					"You have recieved a party invite from %player%. [Click To Accept]",
					CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Accept]"),
			BROADCAST_PARTY_OPEN = new ClickableChatMessage(
					"%player% has opened their party. [Click To Join]",
					CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Join]");
	// Title
	public static final ChatMessage CONFIG_COMMANDS = new ChatMessage("Config Commands", CC.PRIMARY,
			false),
			ARENA_COMMANDS = new ChatMessage("Arena Commands", CC.PRIMARY,
					false),
			CATAGORY_COMMANDS = new ChatMessage("Catagory Commands", CC.PRIMARY,
					false),
			GAMETYPE_COMMANDS = new ChatMessage("Gametype Commands", CC.PRIMARY,
					false),
			QUEUETYPE_COMMANDS = new ChatMessage("Queuetype Commands", CC.PRIMARY,
					false),
			KIT_EDITOR_COMMANDS = new ChatMessage("Kit Editor Commands", CC.PRIMARY,
					false),
			PARTIES_COMMANDS = new ChatMessage("Parties Commands", CC.PRIMARY,
					false),
			SETTINGS_COMMANDS = new ChatMessage("Settings Config Commands", CC.PRIMARY,
					false),
			PARTY_COMMANDS = new ChatMessage("Party Commands", CC.PRIMARY,
					false);
	// Arena Command
	public static final ChatMessage ARENA_CREATE = new ListElementMessage("/arena create <Name>",
			CC.SECONDARY),
			ARENA_SPAWN = new ListElementMessage("/arena spawn <Arena> <1/2/Waiting>",
					CC.SECONDARY),
			ARENA_DISPLAY = new ListElementMessage("/arena setdisplay <Arena> <&{Colour}>",
					CC.SECONDARY),
			ARENA_LIST = new ListElementMessage("/arena list",
					CC.SECONDARY),
			ARENA_TP = new ListElementMessage("/arena tp <Arena>",
					CC.SECONDARY),
			ARENA_WAITING_LOC = new ListElementMessage("/arena waitinglocation <Arena>",
					CC.SECONDARY),
			ARENA_DELETE = new ListElementMessage("/arena delete <Name>",
					CC.SECONDARY);
	// Catagory Command
	public static final ChatMessage CATAGORY_CREATE = new ListElementMessage("/catagory create <Name>",
			CC.SECONDARY),
			CATAGORY_DISPLAY = new ListElementMessage(
					"/catagory setdisplay <Catagory> <DisplayName>",
					CC.SECONDARY),
			CATAGORY_QUEUE = new ListElementMessage(
					"/catagory queue <Catagory> <Queuetype> <Slot/False>",
					CC.SECONDARY),
			CATAGORY_LIST = new ListElementMessage("/catagory list",
					CC.SECONDARY),
			CATAGORY_ADD = new ListElementMessage("/catagory add <Catagory> <Gametype>",
					CC.SECONDARY),
			CATAGORY_REMOVE = new ListElementMessage("/catagory remove <Catagory> <Gametype>",
					CC.SECONDARY),
			CATAGORY_DELETE = new ListElementMessage("/catagory delete <Name>",
					CC.SECONDARY);
	// Gametype Command
	public static final ChatMessage GAMETYPE_CREATE = new ListElementMessage("/gametype create <Name>",
			CC.SECONDARY),
			GAMETYPE_LOAD_KIT = new ListElementMessage("/gametype loadkit <Name>",
					CC.SECONDARY),
			GAMETYPE_KIT = new ListElementMessage("/gametype kit <Gametype>",
					CC.SECONDARY),
			GAMETYPE_DISPLAY = new ListElementMessage(
					"/gametype setdisplay <Gametype> <DisplayName>",
					CC.SECONDARY),
			GAMETYPE_DAMAGE_TICKS = new ListElementMessage(
					"/gametype nodamageticks <Gametype> <Ticks>",
					CC.SECONDARY),
			GAMETYPE_REGEN = new ListElementMessage("/gametype regen <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_GRIEFING = new ListElementMessage(
					"/gametype griefing <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_QUEUE = new ListElementMessage(
					"/gametype queue <Gametype> <Queuetype> <Slot/False>",
					CC.SECONDARY),
			GAMETYPE_LIST = new ListElementMessage(
					"/gametype list",
					CC.SECONDARY),
			GAMETYPE_BUILD = new ListElementMessage("/gametype build <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_DEADLY_WATER = new ListElementMessage(
					"/gametype deadlywater <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_LOOTING = new ListElementMessage(
					"/gametype looting <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_DAMAGE = new ListElementMessage("/gametype damage <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_HUNGER = new ListElementMessage("/gametype hunger <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_BOXING = new ListElementMessage("/gametype boxing <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_EPEARL = new ListElementMessage("/gametype epearl <Gametype> <Time(s)>",
					CC.SECONDARY),
			GAMETYPE_ARENA = new ListElementMessage(
					"/gametype arena <Gametype>",
					CC.SECONDARY),
			GAMETYPE_EVENT = new ListElementMessage("/gametype event <Gametype> <True/False>",
					CC.SECONDARY),
			GAMETYPE_EVENT_ARENA = new ListElementMessage(
					"/gametype seteventarena <Gametype> <Arena>",
					CC.SECONDARY),
			GAMETYPE_ARENA_FOR_ALL = new ListElementMessage(
					"/gametype enablearenaforall <Arena> <True/False>",
					CC.SECONDARY),
			GAMETYPE_DELETE = new ListElementMessage("/gametype delete <Gametype>",
					CC.SECONDARY);
	// Kit Editor Command
	public static final ChatMessage KIT_EDITOR_ENABLE = new ListElementMessage("/kiteditor enable <True/False>",
			CC.SECONDARY),
			KIT_EDITOR_DISPLAY = new ListElementMessage("/kiteditor setdisplay <DisplayName>",
					CC.SECONDARY),
			KIT_EDITOR_SLOT = new ListElementMessage("/kiteditor slot <Slot>",
					CC.SECONDARY),
			KIT_EDITOR_LOCATION = new ListElementMessage("/kiteditor setlocation",
					CC.SECONDARY);
	// List Config Commands
	public static final ChatMessage QUEUETYPE = new ListElementMessage("/queuetype",
			CC.SECONDARY),
			GAMETYPE = new ListElementMessage("/gametype",
					CC.SECONDARY),
			CATAGORY = new ListElementMessage("/catagory",
					CC.SECONDARY),
			AIM_TRAINER = new ListElementMessage("/aimtrainer",
					CC.SECONDARY),
			PVP_BOTS = new ListElementMessage("/pvpbots",
					CC.SECONDARY),
			EVENTS = new ListElementMessage("/events",
					CC.SECONDARY),
			ARENA = new ListElementMessage("/arena",
					CC.SECONDARY),
			KIT_EDITOR = new ListElementMessage("/kiteditor", CC.SECONDARY),
			PARTIES = new ListElementMessage("/parties", CC.SECONDARY),
			LOBBY = new ListElementMessage("/lobby", CC.SECONDARY),
			SETTINGS_CONFIG = new ListElementMessage("/settingsconfig", CC.SECONDARY);
	// Parties Command
	public static final ChatMessage PARTIES_ENABLE = new ListElementMessage("/parties enable <True/False>",
			CC.SECONDARY),
			PARTIES_DISPLAY = new ListElementMessage("/parties setdisplay <DisplayName>",
					CC.SECONDARY),
			PARTIES_SLOT = new ListElementMessage("/parties slot <Slot>",
					CC.SECONDARY);
	// Queuetype Command
	public static final ChatMessage QUEUETYPE_CREATE = new ListElementMessage("/queuetype create <Name>",
			CC.SECONDARY),
			QUEUETYPE_DISPLAY = new ListElementMessage(
					"/queuetype setdisplay <Queuetype> <DisplayName>",
					CC.SECONDARY),
			QUEUETYPE_RANKED = new ListElementMessage(
					"/queuetype ranked <Queuetype> <True/False>",
					CC.SECONDARY),
			QUEUETYPE_SLOT = new ListElementMessage("/queuetype slot <Queuetype> <Slot>",
					CC.SECONDARY),
			QUEUETYPE_LIST = new ListElementMessage(
					"/queuetype list",
					CC.SECONDARY),
			QUEUETYPE_KB = new ListElementMessage(
					"/queuetype kb <Queuetype> <KnockbackProfile>",
					CC.SECONDARY),
			QUEUETYPE_ARENA = new ListElementMessage(
					"/queuetype arena <Queuetype>",
					CC.SECONDARY),
			QUEUETYPE_DELETE = new ListElementMessage("/queuetype delete <Queuetype>",
					CC.SECONDARY);
	// Settings Config Command
	public static final ChatMessage SETTINGS_ENABLE = new ListElementMessage("/settingsconfig enable <True/False>",
			CC.SECONDARY),
			SETTINGS_DISPLAY = new ListElementMessage(
					"/settingsconfig setdisplay <DisplayName>",
					CC.SECONDARY),
			SETTINGS_SLOT = new ListElementMessage("/settingsconfig slot <Slot>",
					CC.SECONDARY);
	// Party Command
	public static final ChatMessage PARTY_CREATE = new ListElementMessage("/party create", CC.SECONDARY),
			PARTY_INVITE = new ListElementMessage("/party invite <Player>", CC.SECONDARY),
			PARTY_OPEN = new ListElementMessage("/party open", CC.SECONDARY),
			PARTY_LIST = new ListElementMessage("/party list", CC.SECONDARY),
			PARTY_JOIN = new ListElementMessage("/party join <PartyLeader>", CC.SECONDARY),
			PARTY_DUEL = new ListElementMessage("/duel <PartyLeader>", CC.SECONDARY),
			PARTY_ACCEPT = new ListElementMessage("/party accept <PartyLeader>", CC.SECONDARY),
			PARTY_LEAVE = new ListElementMessage("/party leave", CC.SECONDARY),
			PARTY_DISBAND = new ListElementMessage("/party disband", CC.SECONDARY);
	// Events
	public static final HoverEvent CLICK_TO_ACCEPT = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
			new ComponentBuilder(CC.GREEN + "Click To Accept")
					.create()),
			CLICK_TO_JOIN = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					new ComponentBuilder(CC.GREEN + "Click To Join").create());
}
