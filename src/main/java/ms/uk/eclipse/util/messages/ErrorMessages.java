package ms.uk.eclipse.util.messages;

import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.Message;

public class ErrorMessages {
        // Arena
        public static final Message ARENA_ALREADY_EXISTS = new ErrorMessage("That arena already exists");
        public static final Message ARENA_DOES_NOT_EXIST = new ErrorMessage("That arena does not exist");
        public static final Message CANNOT_TELEPORT_TO_ARENA = new ErrorMessage(
                        "There was a problem teleporting to that arena");
        // Catagory
        public static final Message CATAGORY_ALREADY_EXISTS = new ErrorMessage("That catagory already exists");
        public static final Message CATAGORY_DOES_NOT_EXIST = new ErrorMessage("That catagory does not exist");
        // Queuetype
        public static final Message QUEUETYPE_ALREADY_EXISTS = new ErrorMessage("That queuetype already exists");
        public static final Message QUEUETYPE_DOES_NOT_EXIST = new ErrorMessage("That queuetype does not exist");
        // Gametype
        public static final Message GAMETYPE_ALREADY_EXISTS = new ErrorMessage("That gametype already exists");
        public static final Message GAMETYPE_DOES_NOT_EXIST = new ErrorMessage("That gametype does not exist");
        // Knockback
        public static final Message KNOCKBACK_DOES_NOT_EXIST = new ErrorMessage("That knockback does not exist");
        // Player
        public static final Message YOU_ARE_NOT_IN_LOBBY = new ErrorMessage("You are not in the lobby");
        public static final Message PLAYER_NOT_ONLINE = new ErrorMessage("That player is not online");
        // Duel
        public static final Message DUEL_SENDER_NOT_ONLINE = new ErrorMessage("The duel sender is not online");
        public static final Message DUEL_SENDER_NOT_IN_LOBBY = new ErrorMessage("The duel sender is not in the lobby");
        public static final Message YOU_CAN_NOT_DUEL_YOURSELF = new ErrorMessage("You can not duel yourself");
        // Party
        public static final Message PLAYER_NOT_IN_PARTY_OR_PARTY_LEADER = new ErrorMessage(
                        "That player is in a party or not a party leader");
        public static final Message YOU_ARE_ALREADY_IN_PARTY = new ErrorMessage("You are already in a party");
        public static final Message YOU_ARE_NOT_IN_PARTY = new ErrorMessage("You are not in a party");
        public static final Message PLAYER_IN_PARTY = new ErrorMessage("That player is in a party");
        public static final Message YOU_ARE_NOT_PARTY_LEADER = new ErrorMessage("You are not the party leader");
        public static final Message PARTY_DOES_NOT_EXIST = new ErrorMessage("That party does not exist");
        public static final Message PARTY_NOT_OPEN = new ErrorMessage("That party is not open");
        public static final Message YOU_CAN_NOT_INVITE_YOURSELF = new ErrorMessage("You can not invite yourself");
}
