package gg.mineral.practice.util.messages.impl;

import gg.mineral.practice.util.messages.UsageMessage;

public class UsageMessages {
	// Arena Command
	public static final UsageMessage ARENA_CREATE = new UsageMessage("/arena create <Name>");
	public static final UsageMessage ARENA_SPAWN = new UsageMessage("/arena spawn <Arena> <1/2/Waiting>");
	public static final UsageMessage ARENA_DISPLAY = new UsageMessage("/arena setdisplay <Arena> <DisplayName>");
	public static final UsageMessage ARENA_TP = new UsageMessage("/arena tp <Arena>");
	public static final UsageMessage ARENA_WAITING_LOC = new UsageMessage("/arena waitinglocation <Arena>");
	public static final UsageMessage ARENA_DELETE = new UsageMessage("/arena delete <Arena>");
	// Catagory Command
	public static final UsageMessage CATAGORY_CREATE = new UsageMessage("/catagory create <Name>");
	public static final UsageMessage CATAGORY_DISPLAY = new UsageMessage(
			"/catagory setdisplay <Catagory> <DisplayName>");
	public static final UsageMessage CATAGORY_QUEUE = new UsageMessage(
			"/catagory queue <Catagory> <Queuetype> <Slot/False>");
	public static final UsageMessage CATAGORY_ADD = new UsageMessage("/catagory add <Catagory> <Gametype>");
	public static final UsageMessage CATAGORY_REMOVE = new UsageMessage("/catagory remove <Catagory> <Gametype>");
	public static final UsageMessage CATAGORY_DELETE = new UsageMessage("/catagory delete <Catagory>");
	// Gametype Command
	public static final UsageMessage GAMETYPE_CREATE = new UsageMessage("/gametype create <Name>");
	public static final UsageMessage GAMETYPE_LOAD_KIT = new UsageMessage("/gametype loadkit <Name>");
	public static final UsageMessage GAMETYPE_KIT = new UsageMessage("/gametype kit <Gametype>");
	public static final UsageMessage GAMETYPE_DISPLAY = new UsageMessage(
			"/gametype setdisplay <Gametype> <DisplayName>");
	public static final UsageMessage GAMETYPE_DAMAGE_TICKS = new UsageMessage(
			"/gametype nodamageticks <Gametype> <Ticks>");
	public static final UsageMessage GAMETYPE_REGEN = new UsageMessage("/gametype regen <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_BOTS = new UsageMessage("/gametype bots <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_GRIEFING = new UsageMessage(
			"/gametype griefing <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_QUEUE = new UsageMessage(
			"/gametype queue <Gametype> <Queuetype> <Slot/False>");
	public static final UsageMessage GAMETYPE_BUILD = new UsageMessage("/gametype build <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_DEADLY_WATER = new UsageMessage(
			"/gametype deadlywater <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_LOOTING = new UsageMessage(
			"/gametype looting <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_DAMAGE = new UsageMessage("/gametype damage <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_HUNGER = new UsageMessage("/gametype hunger <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_BOXING = new UsageMessage("/gametype boxing <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_EPEARL = new UsageMessage("/gametype epearl <Gametype> <Time(s)>");
	public static final UsageMessage GAMETYPE_ARENA = new UsageMessage(
			"/gametype arena <Gametype> <Arena> <True/False>");
	public static final UsageMessage GAMETYPE_EVENT = new UsageMessage("/gametype event <Gametype> <True/False>");
	public static final UsageMessage GAMETYPE_EVENT_ARENA = new UsageMessage(
			"/gametype seteventarena <Gametype> <Arena>");
	public static final UsageMessage GAMETYPE_ARENA_FOR_ALL = new UsageMessage(
			"/gametype enablearenaforall <Arena> <True/False>");
	public static final UsageMessage GAMETYPE_DELETE = new UsageMessage("/gametype delete <Gametype>");
	// Kit Editor Command
	public static final UsageMessage KIT_EDITOR_ENABLE = new UsageMessage("/kiteditor enable <True/False>");
	public static final UsageMessage KIT_EDITOR_DISPLAY = new UsageMessage("/kiteditor setdisplay <DisplayName>");
	public static final UsageMessage KIT_EDITOR_SLOT = new UsageMessage("/kiteditor slot <Slot>");
	// Parties Command
	public static final UsageMessage PARTIES_ENABLE = new UsageMessage("/parties enable <True/False>");
	public static final UsageMessage PARTIES_DISPLAY = new UsageMessage("/parties setdisplay <DisplayName>");
	public static final UsageMessage PARTIES_SLOT = new UsageMessage("/parties slot <Slot>");
	// PvP Bots Command
	public static final UsageMessage PVP_BOTS_ENABLE = new UsageMessage("/pvpbots enable <True/False>");
	public static final UsageMessage PVP_BOTS_DISPLAY = new UsageMessage("/pvpbots setdisplay <DisplayName>");
	public static final UsageMessage PVP_BOTS_SLOT = new UsageMessage("/pvpbots slot <Slot>");
	// Queuetype Command
	public static final UsageMessage QUEUETYPE_CREATE = new UsageMessage("/queuetype create <Name>");
	public static final UsageMessage QUEUETYPE_DISPLAY = new UsageMessage(
			"/queuetype setdisplay <Queuetype> <DisplayName>");
	public static final UsageMessage QUEUETYPE_RANKED = new UsageMessage(
			"/queuetype ranked <Queuetype> <True/False>");
	public static final UsageMessage QUEUETYPE_COMMUNITY = new UsageMessage(
			"/queuetype community <Queuetype> <True/False>");
	public static final UsageMessage QUEUETYPE_UNRANKED = new UsageMessage(
			"/queuetype unranked <Queuetype> <True/False>");
	public static final UsageMessage QUEUETYPE_SLOT = new UsageMessage("/queuetype slot <Queuetype> <Slot>");
	public static final UsageMessage QUEUETYPE_KB = new UsageMessage(
			"/queuetype kb <Queuetype> <KnockbackProfile>");
	public static final UsageMessage QUEUETYPE_BOTS = new UsageMessage(
			"/queuetype bots <Queuetype> <True/False>");
	public static final UsageMessage QUEUETYPE_ARENA = new UsageMessage(
			"/queuetype arena <Queuetype> <Arena> <True/False>");
	public static final UsageMessage QUEUETYPE_DELETE = new UsageMessage("/queuetype delete <Queuetype>");
	// Settings Config Command
	public static final UsageMessage SETTINGS_ENABLE = new UsageMessage("/settingsconfig enable <True/False>");
	public static final UsageMessage SETTINGS_DISPLAY = new UsageMessage(
			"/settingsconfig setdisplay <DisplayName>");
	public static final UsageMessage SETTINGS_SLOT = new UsageMessage("/settingsconfig slot <Slot>");
	// Spectate Config Command
	public static final UsageMessage SPECTATE_ENABLE = new UsageMessage("/spectateconfig enable <True/False>");
	public static final UsageMessage SPECTATE_DISPLAY = new UsageMessage(
			"/spectateconfig setdisplay <DisplayName>");
	public static final UsageMessage SPECTATE_SLOT = new UsageMessage("/spectateconfig slot <Slot>");
	// Leaderboard Config Command
	public static final UsageMessage LEADERBOARD_ENABLE = new UsageMessage("/leaderboardconfig enable <True/False>");
	public static final UsageMessage LEADERBOARD_DISPLAY = new UsageMessage(
			"/leaderboardconfig setdisplay <DisplayName>");
	public static final UsageMessage LEADERBOARD_SLOT = new UsageMessage("/leaderboardconfig slot <Slot>");
	// Accept Command
	public static final UsageMessage ACCEPT = new UsageMessage("/accept <Player>");
	// Duel Command
	public static final UsageMessage DUEL = new UsageMessage("/duel <Player>");
	// Party Command
	public static final UsageMessage PARTY_INVITE = new UsageMessage("/party invite <Name>");
	public static final UsageMessage PARTY_JOIN = new UsageMessage("/party join <Name>");
	public static final UsageMessage PARTY_ACCEPT = new UsageMessage("/party accept <Name>");
	// Follow Command
	public static final UsageMessage FOLLOW = new UsageMessage("/follow <Player>");
	// View Inventory Command
	public static final UsageMessage VIEW_INV = new UsageMessage("/viewinventory <Player>");
	// View Team Inventory Command
	public static final UsageMessage VIEW_TEAM_INV = new UsageMessage("/viewteaminventory <Player>");
	// Join Command
	public static final UsageMessage JOIN = new UsageMessage("/join <Name>");
}
