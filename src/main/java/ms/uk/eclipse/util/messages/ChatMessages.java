package ms.uk.eclipse.util.messages;

import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.ClickableChatMessage;
import ms.uk.eclipse.core.utils.message.ListElementMessage;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class ChatMessages {
	// Value Set
	public static final ChatMessage ARENA_CREATED = new ChatMessage("The %arena% arena has been created.", CC.YELLOW)
			.highlightText(CC.GOLD, "%arena%");
	public static final ChatMessage ARENA_SPAWN_SET = new ChatMessage(
			"The spawn location for the %arena% arena has been set to your location.", CC.YELLOW)
					.highlightText(CC.GOLD, "%arena%", "your location");
	public static final ChatMessage ARENA_DISPLAY_SET = new ChatMessage(
			"The display item for the %arena% arena has been set to the item in your hand.", CC.YELLOW)
					.highlightText(CC.GOLD, "%arena%", "the item in your hand");
	public static final ChatMessage ARENA_DELETED = new ChatMessage("The %arena% arena has been deleted.", CC.YELLOW)
			.highlightText(CC.GOLD, "%arena%");
	public static final ChatMessage CATAGORY_CREATED = new ChatMessage("The %catagory% catagory has been created.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%");
	public static final ChatMessage CATAGORY_DISPLAY_SET = new ChatMessage(
			"The display item for the %catagory% catagory has been set to the item in your hand.", CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%", "the item in your hand");
	public static final ChatMessage CATAGORY_SLOT = new ChatMessage(
			"The slot in the queue for the %catagory% catagory has been set to %slot%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%", "%slot%");
	public static final ChatMessage CATAGORY_ADDED = new ChatMessage(
			"The %gametype% gametype has been added to the %catagory% catagory.", CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%", "%gametype%");
	public static final ChatMessage CATAGORY_REMOVED = new ChatMessage(
			"The %gametype% gametype has been removed from the %catagory% catagory.", CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%", "%gametype%");
	public static final ChatMessage CATAGORY_DELETED = new ChatMessage("The %catagory% catagory has been deleted.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%catagory%");
	public static final ChatMessage GAMETYPE_CREATED = new ChatMessage("The %gametype% gametype has been created.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%");
	public static final ChatMessage GAMETYPE_DISPLAY_SET = new ChatMessage(
			"The display item for the %gametype% gametype has been set to the item in your hand.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "the item in your hand");
	public static final ChatMessage GAMETYPE_DAMAGE_TICKS_SET = new ChatMessage(
			"The hit delay for the %gametype% gametype has been set to the %delay%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%delay%");
	public static final ChatMessage GAMETYPE_REGEN_SET = new ChatMessage(
			"Regeneration for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_GRIEFING_SET = new ChatMessage(
			"Griefing for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_BUILD_SET = new ChatMessage(
			"Build for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_DEADLY_WATER_SET = new ChatMessage(
			"Deadly water for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_LOOTING_SET = new ChatMessage(
			"Looting for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_DAMAGE_SET = new ChatMessage(
			"Damage for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_HUNGER_SET = new ChatMessage(
			"Hunger for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_EVENT_SET = new ChatMessage(
			"Event mode for the %gametype% gametype has been set to %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_LOADED_KIT = new ChatMessage(
			"You have been given the kit for the %gametype% gametype.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%");
	public static final ChatMessage GAMETYPE_KIT_SET = new ChatMessage(
			"The kit for the %gametype% gametype has been set to your inventory contents.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "your inventory contents");
	public static final ChatMessage GAMETYPE_SLOT_SET = new ChatMessage(
			"The slot for the %gametype% gametype has been set to %slot% for the %queuetype% queue.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%queuetype%", "%slot%");
	public static final ChatMessage GAMETYPE_PEARL_COOLDOWN_SET = new ChatMessage(
			"The pearl cooldown for the %gametype% gametype has been set to %cooldown%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%cooldown%");
	public static final ChatMessage GAMETYPE_ARENA_SET = new ChatMessage(
			"The %arena% arena has been set to %toggled% for the %gametype% gametype.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%", "%arena%");
	public static final ChatMessage GAMETYPE_EVENT_ARENA_SET = new ChatMessage(
			"The event arena for the %gametype% gametype has been set to the %arena% arena.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%arena%");
	public static final ChatMessage GAMETYPE_ARENA_FOR_ALL_SET = new ChatMessage(
			"The %arena% arena has been set to %toggled% for all gametypes.", CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%", "%toggled%");
	public static final ChatMessage GAMETYPE_DELETED = new ChatMessage("The %gametype% gametype has been deleted.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%gametype%");
	public static final ChatMessage KIT_EDITOR_ENABLED = new ChatMessage("The kit editor has been set to %toggled%.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%toggled%");
	public static final ChatMessage KIT_EDITOR_DISPLAY_SET = new ChatMessage(
			"The kit editor display item has been set to the item in your hand.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "the item in your hand");
	public static final ChatMessage KIT_EDITOR_SLOT_SET = new ChatMessage(
			"The kit editor slot has been set to the %slot% slot.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%slot%");
	public static final ChatMessage KIT_EDITOR_LOCATION_SET = new ChatMessage(
			"The kit editor location has been set to your location.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "your location");
	public static final ChatMessage SPAWN_SET = new ChatMessage(
			"The spawn location for the server has been set to your location.", CC.YELLOW)
					.highlightText(CC.GOLD, "the server", "your location");
	public static final ChatMessage PARTIES_ENABLED = new ChatMessage("The parties feature has been set to %toggled%.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%toggled%");
	public static final ChatMessage PARTIES_DISPLAY_SET = new ChatMessage(
			"The parties display item has been set to the item in your hand.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "the item in your hand");
	public static final ChatMessage PARTIES_SLOT_SET = new ChatMessage(
			"The parties slot has been set to the %slot% slot.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%slot%");
	public static final ChatMessage QUEUETYPE_CREATED = new ChatMessage("The %queuetype% queuetype has been created.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%");
	public static final ChatMessage QUEUETYPE_DISPLAY_SET = new ChatMessage(
			"The display item for the %queuetype% queuetype has been set to the item in your hand.", CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "the item in your hand");
	public static final ChatMessage QUEUETYPE_RANKED_SET = new ChatMessage(
			"Event mode for the %queuetype% queuetype has been set to the %toggled%.", CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "%toggled%");
	public static final ChatMessage QUEUETYPE_SLOT_SET = new ChatMessage(
			"The slot for the %queuetype% queuetype has been set to the %slot% slot.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "%slot%");
	public static final ChatMessage QUEUETYPE_KB_SET = new ChatMessage(
			"The knockback for the %queuetype% queuetype has been set to the %knockback% profile", CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "%knockback%");
	public static final ChatMessage QUEUETYPE_ARENA_SET = new ChatMessage(
			"The %arena% arena has been set to %toggled% for the %queuetype% queuetype.", CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%", "%toggled%", "%arena%");
	public static final ChatMessage QUEUETYPE_DELETED = new ChatMessage("The %queuetype% queuetype has been deleted.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%queuetype%");
	public static final ChatMessage SETTINGS_ENABLED = new ChatMessage("The settings has been set to %toggled%.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%toggled%");
	public static final ChatMessage SETTINGS_DISPLAY_SET = new ChatMessage(
			"The settings display item has been set to the item in your hand.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "the item in your hand");
	public static final ChatMessage SETTINGS_SLOT_SET = new ChatMessage(
			"The settings slot has been set to the %slot% slot.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%slot%");
	public static final ChatMessage PARTY_CREATED = new ChatMessage("You have created a new party.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "party");

	public static final ChatMessage PARTY_OPENED = new ChatMessage(
			"Your party has been %opened%.",
			CC.YELLOW)
					.highlightText(CC.GOLD, "%opened%");

	public static final ChatMessage DUEL_REQUESTS_TOGGLED = new ChatMessage(
			"Your duel requests has now been %toggled%.",
			CC.YELLOW).highlightText(CC.GOLD, "%toggled%");
	public static final ChatMessage VISIBILITY_TOGGLED = new ChatMessage(
			"Your player visibility has now been %toggled%.",
			CC.YELLOW).highlightText(CC.GOLD, "%toggled%");

	public static final ChatMessage STOP_SPECTATING = new ChatMessage(
			"Please type /stopspectating to stop spectating.",
			CC.YELLOW).highlightText(CC.GOLD, "/stopspectating");

	public static final ChatMessage KIT_SAVED = new ChatMessage(
			"Your kit has been saved.",
			CC.YELLOW);

	// Info
	public static final ChatMessage PEARL = new ChatMessage(
			"You can use the ender pearl again in %time% second(s).",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%time%");
	public static final ChatMessage HEALTH = new ChatMessage(
			"%player% now has %health% health remaining.",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%health%", "%player%");
	public static final ChatMessage NO_OPPONENT = new ChatMessage(
			"There was no opponent available for this round. You will play in the next round instead.",
			CC.AQUA);
	public static final ChatMessage SPECTATING_YOUR_MATCH = new ChatMessage(
			"%player% is now spectating your match.",
			CC.AQUA).highlightText(CC.D_AQUA, "%player%");
	public static final ChatMessage SPECTATING_TOURNAMENT = new ChatMessage(
			"You are now spectating the tournament.",
			CC.AQUA).highlightText(CC.D_AQUA, "the tournament");
	public static final ChatMessage WON_TOURNAMENT = new ChatMessage(
			"%player% has won the tournament.",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%");
	public static final ChatMessage ROUND_OVER = new ChatMessage(
			"Round %round% his over. The next round will start in 5 seconds.",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%round%");
	public static final ChatMessage BEGINS_IN = new ChatMessage(
			"The match will begin in %time% second(s).",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%time%");
	public static final ChatMessage MATCH_STARTED = new ChatMessage(
			"The match has started.",
			CC.D_AQUA, true);
	public static final ChatMessage FOLLOWING = new ChatMessage(
			"You are now following %player%.",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%");
	public static final ChatMessage POTS = new ChatMessage(
			"You have %pots% health potions in your inventory.",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%pots%");
	public static final ClickableChatMessage DUEL_REQUEST_RECIEVED = new ClickableChatMessage(
			"You have recieved a duel request from %player%. [Click To Accept]",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Accept]");
	public static final ChatMessage DUEL_REQUEST_SENT = new ChatMessage(
			"You have send a duel request to %player%. They have 30 seconds to accept.",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%");
	public static final ClickableChatMessage BROADCAST_TOURNAMENT = new ClickableChatMessage(
			"%player% has started a tournament. [Click To Join]",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Join]");
	public static final ChatMessage SPECTATING = new ChatMessage(
			"You are now spectating %player%.",
			CC.AQUA).highlightText(CC.D_AQUA, "%player%");
	public static final ChatMessage CAN_NOT_BROADCAST = new ChatMessage(
			"The message to join the party can only be broadcasted once every 20 seconds.",
			CC.AQUA);
	public static final ChatMessage JOINED_PARTY = new ChatMessage(
			"%player% has joined the party.",
			CC.GREEN)
					.highlightText(CC.D_GREEN, "%player%");
	public static final ChatMessage LEFT_PARTY = new ChatMessage(
			"%player% has left the party.",
			CC.RED)
					.highlightText(CC.D_RED, "%player%");

	public static final ChatMessage JOINED_TOURNAMENT = new ChatMessage(
			"%player% has joined the tourmament.",
			CC.GREEN)
					.highlightText(CC.D_GREEN, "%player%");
	public static final ChatMessage LEFT_TOURNAMENT = new ChatMessage(
			"%player% has left the tournament.",
			CC.RED)
					.highlightText(CC.D_RED, "%player%");
	public static final ClickableChatMessage PARTY_REQUEST_RECIEVED = new ClickableChatMessage(
			"You have recieved a party invite from %player%. [Click To Accept]",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Accept]");
	public static final ChatMessage PARTY_REQUEST_SENT = new ChatMessage(
			"You have send a party invite to %player%. They have 30 seconds to accept.",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%");
	public static final ClickableChatMessage BROADCAST_PARTY_OPEN = new ClickableChatMessage(
			"%player% has opened their party. [Click To Join]",
			CC.AQUA)
					.highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Join]");
	// Title
	public static final ChatMessage CONFIG_COMMANDS = new ChatMessage("Config Commands", CC.PRIMARY,
			false);
	public static final ChatMessage ARENA_COMMANDS = new ChatMessage("Arena Commands", CC.PRIMARY,
			false);
	public static final ChatMessage CATAGORY_COMMANDS = new ChatMessage("Catagory Commands", CC.PRIMARY,
			false);
	public static final ChatMessage GAMETYPE_COMMANDS = new ChatMessage("Gametype Commands", CC.PRIMARY,
			false);
	public static final ChatMessage QUEUETYPE_COMMANDS = new ChatMessage("Queuetype Commands", CC.PRIMARY,
			false);
	public static final ChatMessage KIT_EDITOR_COMMANDS = new ChatMessage("Kit Editor Commands", CC.PRIMARY,
			false);
	public static final ChatMessage PARTIES_COMMANDS = new ChatMessage("Parties Commands", CC.PRIMARY,
			false);
	public static final ChatMessage SETTINGS_COMMANDS = new ChatMessage("Settings Config Commands", CC.PRIMARY,
			false);
	public static final ChatMessage PARTY_COMMANDS = new ChatMessage("Party Commands", CC.PRIMARY,
			false);
	// Arena Command
	public static final ChatMessage ARENA_CREATE = new ListElementMessage("/arena create <Name>",
			CC.SECONDARY);
	public static final ChatMessage ARENA_SPAWN = new ListElementMessage("/arena spawn <Arena> <1/2/Waiting>",
			CC.SECONDARY);
	public static final ChatMessage ARENA_DISPLAY = new ListElementMessage("/arena setdisplay <Arena> <&{Colour}>",
			CC.SECONDARY);
	public static final ChatMessage ARENA_LIST = new ListElementMessage("/arena list",
			CC.SECONDARY);
	public static final ChatMessage ARENA_TP = new ListElementMessage("/arena tp <Arena>",
			CC.SECONDARY);
	public static final ChatMessage ARENA_WAITING_LOC = new ListElementMessage("/arena waitinglocation <Arena>",
			CC.SECONDARY);
	public static final ChatMessage ARENA_DELETE = new ListElementMessage("/arena delete <Name>",
			CC.SECONDARY);
	// Catagory Command
	public static final ChatMessage CATAGORY_CREATE = new ListElementMessage("/catagory create <Name>",
			CC.SECONDARY);
	public static final ChatMessage CATAGORY_DISPLAY = new ListElementMessage(
			"/catagory setdisplay <Catagory> <DisplayName>",
			CC.SECONDARY);
	public static final ChatMessage CATAGORY_QUEUE = new ListElementMessage(
			"/catagory queue <Catagory> <Queuetype> <Slot/False>",
			CC.SECONDARY);
	public static final ChatMessage CATAGORY_LIST = new ListElementMessage("/catagory list",
			CC.SECONDARY);
	public static final ChatMessage CATAGORY_ADD = new ListElementMessage("/catagory add <Catagory> <Gametype>",
			CC.SECONDARY);
	public static final ChatMessage CATAGORY_REMOVE = new ListElementMessage("/catagory remove <Catagory> <Gametype>",
			CC.SECONDARY);
	public static final ChatMessage CATAGORY_DELETE = new ListElementMessage("/catagory delete <Name>",
			CC.SECONDARY);
	// Gametype Command
	public static final ChatMessage GAMETYPE_CREATE = new ListElementMessage("/gametype create <Name>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_LOAD_KIT = new ListElementMessage("/gametype loadkit <Name>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_KIT = new ListElementMessage("/gametype kit <Gametype>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_DISPLAY = new ListElementMessage(
			"/gametype setdisplay <Gametype> <DisplayName>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_DAMAGE_TICKS = new ListElementMessage(
			"/gametype nodamageticks <Gametype> <Ticks>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_REGEN = new ListElementMessage("/gametype regen <Gametype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_GRIEFING = new ListElementMessage(
			"/gametype griefing <Gametype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_QUEUE = new ListElementMessage(
			"/gametype queue <Gametype> <Queuetype> <Slot/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_LIST = new ListElementMessage(
			"/gametype list",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_BUILD = new ListElementMessage("/gametype build <Gametype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_DEADLY_WATER = new ListElementMessage(
			"/gametype deadlywater <Gametype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_LOOTING = new ListElementMessage(
			"/gametype looting <Gametype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_DAMAGE = new ListElementMessage("/gametype damage <Gametype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_HUNGER = new ListElementMessage("/gametype hunger <Gametype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_EPEARL = new ListElementMessage("/gametype epearl <Gametype> <Time(s)>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_ARENA = new ListElementMessage(
			"/gametype arena <Gametype> <Arena> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_EVENT = new ListElementMessage("/gametype event <Gametype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_EVENT_ARENA = new ListElementMessage(
			"/gametype seteventarena <Gametype> <Arena>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_ARENA_FOR_ALL = new ListElementMessage(
			"/gametype enablearenaforall <Arena> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE_DELETE = new ListElementMessage("/gametype delete <Gametype>",
			CC.SECONDARY);
	// Kit Editor Command
	public static final ChatMessage KIT_EDITOR_ENABLE = new ListElementMessage("/kiteditor enable <True/False>",
			CC.SECONDARY);
	public static final ChatMessage KIT_EDITOR_DISPLAY = new ListElementMessage("/kiteditor setdisplay <DisplayName>",
			CC.SECONDARY);
	public static final ChatMessage KIT_EDITOR_SLOT = new ListElementMessage("/kiteditor slot <Slot>",
			CC.SECONDARY);
	public static final ChatMessage KIT_EDITOR_LOCATION = new ListElementMessage("/kiteditor setlocation",
			CC.SECONDARY);
	// List Config Commands
	public static final ChatMessage QUEUETYPE = new ListElementMessage("/queuetype",
			CC.SECONDARY);
	public static final ChatMessage GAMETYPE = new ListElementMessage("/gametype",
			CC.SECONDARY);
	public static final ChatMessage CATAGORY = new ListElementMessage("/catagory",
			CC.SECONDARY);
	public static final ChatMessage AIM_TRAINER = new ListElementMessage("/aimtrainer",
			CC.SECONDARY);
	public static final ChatMessage PVP_BOTS = new ListElementMessage("/pvpbots",
			CC.SECONDARY);
	public static final ChatMessage EVENTS = new ListElementMessage("/events",
			CC.SECONDARY);
	public static final ChatMessage ARENA = new ListElementMessage("/arena",
			CC.SECONDARY);
	public static final ChatMessage KIT_EDITOR = new ListElementMessage("/kiteditor",
			CC.SECONDARY);
	public static final ChatMessage PARTIES = new ListElementMessage("/parties",
			CC.SECONDARY);
	public static final ChatMessage LOBBY = new ListElementMessage("/lobby",
			CC.SECONDARY);
	public static final ChatMessage SETTINGS_CONFIG = new ListElementMessage("/settingsconfig",
			CC.SECONDARY);
	// Parties Command
	public static final ChatMessage PARTIES_ENABLE = new ListElementMessage("/parties enable <True/False>",
			CC.SECONDARY);
	public static final ChatMessage PARTIES_DISPLAY = new ListElementMessage("/parties setdisplay <DisplayName>",
			CC.SECONDARY);
	public static final ChatMessage PARTIES_SLOT = new ListElementMessage("/parties slot <Slot>",
			CC.SECONDARY);
	// Queuetype Command
	public static final ChatMessage QUEUETYPE_CREATE = new ListElementMessage("/queuetype create <Name>",
			CC.SECONDARY);
	public static final ChatMessage QUEUETYPE_DISPLAY = new ListElementMessage(
			"/queuetype setdisplay <Queuetype> <DisplayName>",
			CC.SECONDARY);
	public static final ChatMessage QUEUETYPE_RANKED = new ListElementMessage(
			"/queuetype ranked <Queuetype> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage QUEUETYPE_SLOT = new ListElementMessage("/queuetype slot <Queuetype> <Slot>",
			CC.SECONDARY);
	public static final ChatMessage QUEUETYPE_LIST = new ListElementMessage(
			"/queuetype list",
			CC.SECONDARY);
	public static final ChatMessage QUEUETYPE_KB = new ListElementMessage(
			"/queuetype kb <Queuetype> <KnockbackProfile>",
			CC.SECONDARY);
	public static final ChatMessage QUEUETYPE_ARENA = new ListElementMessage(
			"/queuetype arena <Queuetype> <Arena> <True/False>",
			CC.SECONDARY);
	public static final ChatMessage QUEUETYPE_DELETE = new ListElementMessage("/queuetype delete <Queuetype>",
			CC.SECONDARY);
	// Settings Config Command
	public static final ChatMessage SETTINGS_ENABLE = new ListElementMessage("/settingsconfig enable <True/False>",
			CC.SECONDARY);
	public static final ChatMessage SETTINGS_DISPLAY = new ListElementMessage(
			"/settingsconfig setdisplay <DisplayName>",
			CC.SECONDARY);
	public static final ChatMessage SETTINGS_SLOT = new ListElementMessage("/settingsconfig slot <Slot>",
			CC.SECONDARY);
	// Party Command
	public static final ChatMessage PARTY_CREATE = new ListElementMessage("/party create", CC.SECONDARY);
	public static final ChatMessage PARTY_INVITE = new ListElementMessage("/party invite <Player>", CC.SECONDARY);
	public static final ChatMessage PARTY_OPEN = new ListElementMessage("/party open", CC.SECONDARY);
	public static final ChatMessage PARTY_LIST = new ListElementMessage("/party list", CC.SECONDARY);
	public static final ChatMessage PARTY_JOIN = new ListElementMessage("/party join <PartyLeader>", CC.SECONDARY);
	public static final ChatMessage PARTY_DUEL = new ListElementMessage("/duel <PartyLeader>", CC.SECONDARY);
	public static final ChatMessage PARTY_ACCEPT = new ListElementMessage("/party accept <PartyLeader>", CC.SECONDARY);
	public static final ChatMessage PARTY_LEAVE = new ListElementMessage("/party leave", CC.SECONDARY);
	public static final ChatMessage PARTY_DISBAND = new ListElementMessage("/party disband", CC.SECONDARY);
	// Events
	public static final HoverEvent CLICK_TO_ACCEPT = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
			new ComponentBuilder(CC.GREEN + "Click To Accept")
					.create());
	public static final HoverEvent CLICK_TO_JOIN = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
			new ComponentBuilder(CC.GREEN + "Click To Join")
					.create());
}
