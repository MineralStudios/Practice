package gg.mineral.practice.util.messages.impl

import gg.mineral.practice.util.messages.ErrorMessage
import gg.mineral.practice.util.messages.Message

object ErrorMessages {
    val ALREADY_IN_QUEUE: Message = ErrorMessage("You are already in a queue.")
    val CANNOT_FORFEIT_RANKED: Message = ErrorMessage("You can not leave ranked matches.")

    // Arena
    val ARENA_ALREADY_EXISTS: Message = ErrorMessage("That arena already exists.")
    val ARENA_DOES_NOT_EXIST: Message = ErrorMessage("That arena does not exist.")
    val CANNOT_TELEPORT_TO_ARENA: Message = ErrorMessage(
        "There was a problem teleporting to that arena."
    )
    val MAX_TNT: Message = ErrorMessage("You have reached the maximum tnt limit.")
    val ARENA_NOT_FOUND: Message = ErrorMessage("An arena could not be found.")

    // Category
    val CATEGORY_ALREADY_EXISTS: Message = ErrorMessage("That category already exists.")
    val CATEGORY_DOES_NOT_EXIST: Message = ErrorMessage("That category does not exist.")

    // Queuetype
    val QUEUETYPE_ALREADY_EXISTS: Message = ErrorMessage("That queuetype already exists.")
    val QUEUETYPE_DOES_NOT_EXIST: Message = ErrorMessage("That queuetype does not exist.")

    // Gametype
    val GAMETYPE_ALREADY_EXISTS: Message = ErrorMessage("That gametype already exists.")
    val GAMETYPE_DOES_NOT_EXIST: Message = ErrorMessage("That gametype does not exist.")

    // Knockback
    val KNOCKBACK_DOES_NOT_EXIST: Message = ErrorMessage("That knockback does not exist.")

    // Player
    val YOU_ARE_NOT_IN_LOBBY: Message = ErrorMessage("You are not in the lobby.")
    val PLAYER_NOT_ONLINE: Message = ErrorMessage("That player is not online.")
    val PLAYER_NOT_IN_LOBBY: Message = ErrorMessage("That player is not in the lobby.")
    val CAN_NOT_LEAVE_YET: Message = ErrorMessage("You can not leave yet.")

    // Duel
    val DUEL_SENDER_NOT_ONLINE: Message = ErrorMessage("The duel sender is not online.")
    val DUEL_SENDER_NOT_IN_LOBBY: Message = ErrorMessage("The duel sender is not in the lobby.")
    val YOU_CAN_NOT_DUEL_YOURSELF: Message = ErrorMessage("You can not duel yourself.")
    val DUEL_REQUESTS_DISABLED: Message = ErrorMessage(
        "That player has duel requests disabled."
    )
    val DUEL_REQUEST_ALREADY_SENT: Message = ErrorMessage(
        "You have already sent that player a duel request."
    )
    val WAIT_TO_REQUEST: Message = ErrorMessage(
        "You need to wait before sending another duel request."
    )

    // Party
    val PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER: Message = ErrorMessage(
        "That player is in a party or not a party leader."
    )
    val NOT_IN_PARTY_OR_PARTY_LEADER: Message = ErrorMessage(
        "You are not in a party or not a party leader."
    )
    val YOU_ARE_ALREADY_IN_PARTY: Message = ErrorMessage("You are already in a party.")
    val YOU_ARE_NOT_IN_PARTY: Message = ErrorMessage("You are not in a party.")
    val PLAYER_IN_PARTY: Message = ErrorMessage("That player is in a party.")
    val YOU_ARE_NOT_PARTY_LEADER: Message = ErrorMessage("You are not the party leader.")
    val PARTY_DOES_NOT_EXIST: Message = ErrorMessage("That party does not exist.")
    val PARTY_NOT_BIG_ENOUGH: Message = ErrorMessage(
        "You need to be in a party with at least 2 people."
    )
    val PARTY_NOT_OPEN: Message = ErrorMessage("That party is not open.")
    val PARTY_REQUESTS_DISABLED: Message = ErrorMessage(
        "That player has party requests disabled."
    )
    val YOU_CAN_NOT_INVITE_YOURSELF: Message = ErrorMessage("You can not invite yourself.")
    val REQUEST_SENDER_NOT_ONLINE: Message = ErrorMessage("The request sender is not online.")
    val REQUEST_EXPIRED: Message = ErrorMessage("That party request has expired.")
    val WAIT_TO_INVITE: Message = ErrorMessage("You need to wait before sending another invite.")

    // Spectate/Follow
    val NOT_FOLLOW_SELF: Message = ErrorMessage("You can not follow yourself.")
    val NOT_SPEC_SELF: Message = ErrorMessage("You can not spectate yourself.")
    val NOT_FOLLOWING: Message = ErrorMessage("You are not following anyone.")
    val NOT_SPEC: Message = ErrorMessage("You are not spectating anyone.")

    // Match
    val PLAYER_NOT_IN_MATCH: Message = ErrorMessage("That player is not in a match.")
    val PLAYER_NOT_IN_MATCH_OR_EVENT: Message = ErrorMessage(
        "That player is not in a match or event."
    )
    val IN_MATCH: Message = ErrorMessage("You are already in a match.")
    val NOT_IN_MATCH: Message = ErrorMessage("You are not in a match.")

    // Inventory
    val PLAYER_INVENTORY_NOT_FOUND: Message = ErrorMessage(
        "That player's inventory was not found"
    )
    val TEAM_INVENTORY_NOT_FOUND: Message = ErrorMessage(
        "That team's inventory was not found"
    )

    // Tournament
    val ALREADY_IN_TOURNAMENT: Message = ErrorMessage("You are already in a tournament.")
    val TOURNAMENT_STARTED: Message = ErrorMessage("That tournament has already started.")
    val NOT_IN_TOURNAMENT: Message = ErrorMessage("You are not in a tournament.")
    val TOURNAMENT_NOT_ENOUGH_PLAYERS: Message = ErrorMessage(
        "There was not enough players to start the tournament."
    )
    val TOURNAMENT_FULL: Message = ErrorMessage("The tournament is full.")
    val ALREADY_IN_EVENT: Message = ErrorMessage("You are already in an event.")
    val EVENT_FULL: Message = ErrorMessage("The event is full.")
    val NOT_IN_EVENT: Message = ErrorMessage("You are not in an event.")
    val EVENT_TOURNAMENT_NOT_EXIST: Message = ErrorMessage("That event/tournament no longer exists.")
    val EVENT_STARTED: Message = ErrorMessage("That event has already started.")
    val EVENT_NOT_ENOUGH_PLAYERS: Message = ErrorMessage(
        "There was not enough players to start the event."
    )

    // Kit Editor
    val KIT_EDITOR_LOCATION_NOT_SET: Message = ErrorMessage(
        "A kit editor location has not been set, use /kiteditor setlocation to set the location."
    )
    val NOT_IN_KIT_EDITOR_OR_CREATOR: Message = ErrorMessage(
        "You are not in the kit editor of kit creator."
    )
    val ITEM_LIMIT: Message = ErrorMessage("The limit for this item has been reached.")

    // Other
    val INVALID_SLOT: Message = ErrorMessage("You did not input a valid slot.")
    val INVALID_NUMBER: Message = ErrorMessage("You did not input a valid number.")
    val COMING_SOON: Message = ErrorMessage("This feature is not yet released, it will be completed soon.")
    val RANK_REQUIRED: Message = ErrorMessage("A rank is required to access this feature.")
}
