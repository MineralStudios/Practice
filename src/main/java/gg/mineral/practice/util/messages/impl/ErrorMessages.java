package gg.mineral.practice.util.messages.impl;

import gg.mineral.practice.util.messages.ErrorMessage;
import gg.mineral.practice.util.messages.Message;

public class ErrorMessages {
	// Arena
	public static final Message ARENA_ALREADY_EXISTS = new ErrorMessage("That arena already exists."),
			ARENA_DOES_NOT_EXIST = new ErrorMessage("That arena does not exist."),
			CANNOT_TELEPORT_TO_ARENA = new ErrorMessage(
					"There was a problem teleporting to that arena."),
			MAX_TNT = new ErrorMessage("You have reached the maximum tnt limit."),
			ARENA_NOT_FOUND = new ErrorMessage("An arena could not be found.");
	// Catagory
	public static final Message CATAGORY_ALREADY_EXISTS = new ErrorMessage("That catagory already exists."),
			CATAGORY_DOES_NOT_EXIST = new ErrorMessage("That catagory does not exist.");
	// Queuetype
	public static final Message QUEUETYPE_ALREADY_EXISTS = new ErrorMessage("That queuetype already exists."),
			QUEUETYPE_DOES_NOT_EXIST = new ErrorMessage("That queuetype does not exist.");
	// Gametype
	public static final Message GAMETYPE_ALREADY_EXISTS = new ErrorMessage("That gametype already exists."),
			GAMETYPE_DOES_NOT_EXIST = new ErrorMessage("That gametype does not exist.");
	// Knockback
	public static final Message KNOCKBACK_DOES_NOT_EXIST = new ErrorMessage("That knockback does not exist.");
	// Player
	public static final Message YOU_ARE_NOT_IN_LOBBY = new ErrorMessage("You are not in the lobby."),
			PLAYER_NOT_ONLINE = new ErrorMessage("That player is not online."),
			PLAYER_NOT_IN_LOBBY = new ErrorMessage("That player is not in the lobby."),
			CAN_NOT_LEAVE_YET = new ErrorMessage("You can not leave yet.");
	// Duel
	public static final Message DUEL_SENDER_NOT_ONLINE = new ErrorMessage("The duel sender is not online."),
			DUEL_SENDER_NOT_IN_LOBBY = new ErrorMessage("The duel sender is not in the lobby."),
			YOU_CAN_NOT_DUEL_YOURSELF = new ErrorMessage("You can not duel yourself."),
			DUEL_REQUESTS_DISABLED = new ErrorMessage(
					"That player has duel requests disabled."),
			DUEL_REQUEST_ALREADY_SENT = new ErrorMessage(
					"You have already sent that player a duel request."),
			WAIT_TO_REQUEST = new ErrorMessage(
					"You need to wait before sending another duel request.");
	// Party
	public static final Message PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER = new ErrorMessage(
			"That player is in a party or not a party leader."),
			YOU_ARE_ALREADY_IN_PARTY = new ErrorMessage("You are already in a party."),
			YOU_ARE_NOT_IN_PARTY = new ErrorMessage("You are not in a party."),
			PLAYER_IN_PARTY = new ErrorMessage("That player is in a party."),
			YOU_ARE_NOT_PARTY_LEADER = new ErrorMessage("You are not the party leader."),
			PARTY_DOES_NOT_EXIST = new ErrorMessage("That party does not exist."),
			PARTY_NOT_BIG_ENOUGH = new ErrorMessage(
					"You need to be in a party with at least 2 people."),
			PARTY_NOT_OPEN = new ErrorMessage("That party is not open."), PARTY_REQUESTS_DISABLED = new ErrorMessage(
					"That player has party requests disabled."),
			YOU_CAN_NOT_INVITE_YOURSELF = new ErrorMessage("You can not invite yourself."),
			REQUEST_SENDER_NOT_ONLINE = new ErrorMessage("The request sender is not online."),
			REQUEST_EXPIRED = new ErrorMessage("That party request has expired."),
			WAIT_TO_INVITE = new ErrorMessage("You need to wait before sending another invite.");
	// Spectate/Follow
	public static final Message NOT_FOLLOW_SELF = new ErrorMessage("You can not follow yourself."),
			NOT_SPEC_SELF = new ErrorMessage("You can not spectate yourself."),
			NOT_FOLLOWING = new ErrorMessage("You are not following anyone."),
			NOT_SPEC = new ErrorMessage("You are not spectating anyone.");
	// Match
	public static final Message PLAYER_NOT_IN_MATCH = new ErrorMessage("That player is not in a match."),
			PLAYER_NOT_IN_MATCH_OR_EVENT = new ErrorMessage(
					"That player is not in a match or event."),
			NOT_IN_MATCH = new ErrorMessage("You are not in a match.");
	// Inventory
	public static final Message PLAYER_INVENTORY_NOT_FOUND = new ErrorMessage(
			"That player's inventory was not found"),
			TEAM_INVENTORY_NOT_FOUND = new ErrorMessage(
					"That team's inventory was not found");
	// Tournament
	public static final Message ALREADY_IN_TOURNAMENT = new ErrorMessage("You are already in a tournament."),
			TOURNAMENT_STARTED = new ErrorMessage("That tournament has already started."),
			TOURNAMENT_NOT_ENOUGH_PLAYERS = new ErrorMessage(
					"There was not enough players to start the tournament."),
			ALREADY_IN_EVENT = new ErrorMessage("You are already in an event."),
			EVENT_TOURNAMENT_NOT_EXIST = new ErrorMessage("That event/tournament no longer exists."),
			EVENT_STARTED = new ErrorMessage("That event has already started."),
			EVENT_NOT_ENOUGH_PLAYERS = new ErrorMessage(
					"There was not enough players to start the event.");
	// Kit Editor
	public static final Message KIT_EDITOR_LOCATION_NOT_SET = new ErrorMessage(
			"A kit editor location has not been set, use /kiteditor setlocation to set the location."),
			NOT_IN_KIT_EDITOR_OR_CREATOR = new ErrorMessage(
					"You are not in the kit editor of kit creator."),
			ITEM_LIMIT = new ErrorMessage("The limit for this item has been reached.");
	// Other
	public static final Message INVALID_SLOT = new ErrorMessage("You did not input a valid slot."),
			INVALID_NUMBER = new ErrorMessage("You did not input a valid number."),
			COMING_SOON = new ErrorMessage("This feature is not yet released, it will be completed soon."),
			RANK_REQUIRED = new ErrorMessage("A rank is required to access this feature.");

}
