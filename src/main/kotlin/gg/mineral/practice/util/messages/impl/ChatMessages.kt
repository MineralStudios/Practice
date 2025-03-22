package gg.mineral.practice.util.messages.impl

import gg.mineral.practice.util.messages.CC
import gg.mineral.practice.util.messages.ChatMessage
import gg.mineral.practice.util.messages.ClickableChatMessage
import gg.mineral.practice.util.messages.ListElementMessage
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent

object ChatMessages {

    // Value Set
    val ARENA_CREATED: ChatMessage = ChatMessage("The %arena% arena has been created.", CC.YELLOW)
        .highlightText(CC.GOLD, "%arena%")
    val ARENA_SPAWN_SET: ChatMessage = ChatMessage(
        "The spawn location for the %arena% arena has been set to your location.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%arena%", "your location")
    val ARENA_DISPLAY_SET: ChatMessage = ChatMessage(
        "The display item for the %arena% arena has been set to the item in your hand.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%arena%", "the item in your hand")
    val ARENA_DELETED: ChatMessage = ChatMessage("The %arena% arena has been deleted.", CC.YELLOW)
        .highlightText(CC.GOLD, "%arena%")
    val CATEGORY_CREATED: ChatMessage = ChatMessage(
        "The %category% category has been created.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%category%")
    val CATEGORY_DISPLAY_SET: ChatMessage = ChatMessage(
        "The display item for the %category% category has been set to the item in your hand.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%category%", "the item in your hand")
    val CATEGORY_SLOT: ChatMessage = ChatMessage(
        "The slot in the queue for the %category% category has been set to %slot%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%category%", "%slot%")
    val CATEGORY_ADDED: ChatMessage = ChatMessage(
        "The %gametype% gametype has been added to the %category% category.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%category%", "%gametype%")
    val CATEGORY_REMOVED: ChatMessage = ChatMessage(
        "The %gametype% gametype has been removed from the %category% category.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%category%", "%gametype%")
    val CATEGORY_DELETED: ChatMessage = ChatMessage(
        "The %category% category has been deleted.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%category%")
    val GAMETYPE_CREATED: ChatMessage = ChatMessage(
        "The %gametype% gametype has been created.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%")
    val GAMETYPE_DISPLAY_SET: ChatMessage = ChatMessage(
        "The display item for the %gametype% gametype has been set to the item in your hand.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "the item in your hand")
    val GAMETYPE_DAMAGE_TICKS_SET: ChatMessage = ChatMessage(
        "The hit delay for the %gametype% gametype has been set to the %delay%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%delay%")
    val GAMETYPE_BUILD_LIMIT_SET: ChatMessage = ChatMessage(
        "The build limit for the %gametype% gametype has been set to the %limit%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%limit%")
    val GAMETYPE_REGEN_SET: ChatMessage = ChatMessage(
        "Regeneration for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_BOTS_SET: ChatMessage = ChatMessage(
        "Bots for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_GRIEFING_SET: ChatMessage = ChatMessage(
        "Griefing for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_BUILD_SET: ChatMessage = ChatMessage(
        "Build for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_DEADLY_WATER_SET: ChatMessage = ChatMessage(
        "Deadly water for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_LOOTING_SET: ChatMessage = ChatMessage(
        "Looting for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_DAMAGE_SET: ChatMessage = ChatMessage(
        "Damage for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_HUNGER_SET: ChatMessage = ChatMessage(
        "Hunger for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_BOXING_SET: ChatMessage = ChatMessage(
        "Boxing for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_EVENT_SET: ChatMessage = ChatMessage(
        "Event mode for the %gametype% gametype has been set to %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_LOADED_KIT: ChatMessage = ChatMessage(
        "You have been given the kit for the %gametype% gametype.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%")
    val GAMETYPE_KIT_SET: ChatMessage = ChatMessage(
        "The kit for the %gametype% gametype has been set to your inventory contents.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "your inventory contents")
    val GAMETYPE_SLOT_SET: ChatMessage = ChatMessage(
        "The slot for the %gametype% gametype has been set to %slot% for the %queuetype% queue.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%queuetype%", "%slot%")
    val GAMETYPE_PEARL_COOLDOWN_SET: ChatMessage = ChatMessage(
        "The pearl cooldown for the %gametype% gametype has been set to %cooldown%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%cooldown%")
    val GAMETYPE_ARENA_SET: ChatMessage = ChatMessage(
        "The %arena% arena has been set to %toggled% for the %gametype% gametype.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%", "%arena%")
    val GAMETYPE_EVENT_ARENA_SET: ChatMessage = ChatMessage(
        "The event arena for the %gametype% gametype has been set to the %arena% arena.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%arena%")
    val GAMETYPE_ARENA_FOR_ALL_SET: ChatMessage = ChatMessage(
        "The %arena% arena has been set to %toggled% for all gametypes.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%", "%toggled%")
    val GAMETYPE_DELETED: ChatMessage = ChatMessage(
        "The %gametype% gametype has been deleted.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%gametype%")
    val KIT_EDITOR_ENABLED: ChatMessage = ChatMessage(
        "The kit editor has been set to %toggled%.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%toggled%")
    val KIT_EDITOR_DISPLAY_SET: ChatMessage = ChatMessage(
        "The kit editor display item has been set to the item in your hand.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "the item in your hand")
    val KIT_EDITOR_SLOT_SET: ChatMessage = ChatMessage(
        "The kit editor slot has been set to the %slot% slot.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%slot%")
    val KIT_EDITOR_LOCATION_SET: ChatMessage = ChatMessage(
        "The kit editor location has been set to your location.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "your location")
    val SPAWN_SET: ChatMessage = ChatMessage(
        "The spawn location for the server has been set to your location.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "the server", "your location")
    val PARTIES_ENABLED: ChatMessage = ChatMessage(
        "The parties feature has been set to %toggled%.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%toggled%")
    val PARTIES_DISPLAY_SET: ChatMessage = ChatMessage(
        "The parties display item has been set to the item in your hand.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "the item in your hand")
    val PARTIES_SLOT_SET: ChatMessage = ChatMessage(
        "The parties slot has been set to the %slot% slot.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%slot%")
    val QUEUETYPE_CREATED: ChatMessage = ChatMessage(
        "The %queuetype% queuetype has been created.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%")
    val QUEUETYPE_DISPLAY_SET: ChatMessage = ChatMessage(
        "The display item for the %queuetype% queuetype has been set to the item in your hand.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%", "the item in your hand")
    val QUEUETYPE_RANKED_SET: ChatMessage = ChatMessage(
        "Ranked for the %queuetype% queuetype has been set to the %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%", "%toggled%")
    val QUEUETYPE_COMMUNITY_SET: ChatMessage = ChatMessage(
        "Community for the %queuetype% queuetype has been set to the %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%", "%toggled%")
    val QUEUETYPE_UNRANKED_SET: ChatMessage = ChatMessage(
        "Unranked for the %queuetype% queuetype has been set to the %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%", "%toggled%")
    val QUEUETYPE_BOTS_SET: ChatMessage = ChatMessage(
        "Bots for the %queuetype% queuetype has been set to the %toggled%.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%", "%toggled%")
    val QUEUETYPE_SLOT_SET: ChatMessage = ChatMessage(
        "The slot for the %queuetype% queuetype has been set to the %slot% slot.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%", "%slot%")
    val QUEUETYPE_KB_SET: ChatMessage = ChatMessage(
        "The knockback for the %queuetype% queuetype has been set to the %knockback% profile", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%", "%knockback%")
    val QUEUETYPE_ARENA_SET: ChatMessage = ChatMessage(
        "The %arena% arena has been set to %toggled% for the %queuetype% queuetype.", CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%", "%toggled%", "%arena%")
    val QUEUETYPE_DELETED: ChatMessage = ChatMessage(
        "The %queuetype% queuetype has been deleted.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%queuetype%")
    val SETTINGS_ENABLED: ChatMessage = ChatMessage(
        "The settings has been set to %toggled%.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%toggled%")
    val SETTINGS_DISPLAY_SET: ChatMessage = ChatMessage(
        "The settings display item has been set to the item in your hand.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "the item in your hand")
    val SETTINGS_SLOT_SET: ChatMessage = ChatMessage(
        "The settings slot has been set to the %slot% slot.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%slot%")
    val SPECTATE_ENABLED: ChatMessage = ChatMessage(
        "The spectator item has been set to %toggled%.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%toggled%")
    val SPECTATE_DISPLAY_SET: ChatMessage = ChatMessage(
        "The spectate display item has been set to the item in your hand.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "the item in your hand")
    val SPECTATE_SLOT_SET: ChatMessage = ChatMessage(
        "The spectate slot has been set to the %slot% slot.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%slot%")

    val OLD_COMBAT_ENABLED: ChatMessage = ChatMessage(
        "Old combat mode has been set to %toggled%.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%toggled%")

    val LEADERBOARD_ENABLED: ChatMessage = ChatMessage(
        "The leaderboard item has been set to %toggled%.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%toggled%")

    val LEADERBOARD_DISPLAY_SET: ChatMessage = ChatMessage(
        "The leaderboard display item has been set to the item in your hand.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "the item in your hand")
    val LEADERBOARD_SLOT_SET: ChatMessage = ChatMessage(
        "The leaderboard slot has been set to the %slot% slot.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%slot%")
    val PARTY_CREATED: ChatMessage = ChatMessage(
        "You have created a new party.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "party")
    val PARTY_OPENED: ChatMessage = ChatMessage(
        "Your party has been %opened%.",
        CC.YELLOW
    )
        .highlightText(CC.GOLD, "%opened%")
    val DUEL_REQUESTS_TOGGLED: ChatMessage = ChatMessage(
        "Your duel requests has now been %toggled%.",
        CC.YELLOW
    ).highlightText(CC.GOLD, "%toggled%")
    val PARTY_REQUESTS_TOGGLED: ChatMessage = ChatMessage(
        "Your party requests has now been %toggled%.",
        CC.YELLOW
    ).highlightText(CC.GOLD, "%toggled%")
    val SCOREBOARD_TOGGLED: ChatMessage = ChatMessage(
        "Your scoreboard has now been %toggled%.",
        CC.YELLOW
    ).highlightText(CC.GOLD, "%toggled%")

    val VISIBILITY_TOGGLED: ChatMessage = ChatMessage(
        "Your player visibility has now been %toggled%.",
        CC.YELLOW
    ).highlightText(CC.GOLD, "%toggled%")
    val KIT_SAVED: ChatMessage = ChatMessage(
        "Your kit has been saved.",
        CC.YELLOW
    )
    val KIT_DELETED: ChatMessage = ChatMessage(
        "Your kit has been deleted.",
        CC.YELLOW
    )

    // Info
    val PEARL: ChatMessage = ChatMessage(
        "You can use the ender pearl again in %time% second(s).",
        CC.AQUA
    )
        .highlightText(CC.D_AQUA, "%time%")
    val JOINED_QUEUE: ChatMessage = ChatMessage(
        "You are now queued for %queue%%category% %gametype%.",
        CC.AQUA
    )
        .highlightText(CC.D_AQUA, "%queue%", "%category%", "%gametype%")
    val STOP_SPECTATING: ChatMessage = ChatMessage(
        "Please type /stopspectating to stop spectating.",
        CC.YELLOW
    ).highlightText(CC.GOLD, "/stopspectating")
    val LEAVE_KIT_EDITOR: ChatMessage = ChatMessage(
        "Please type /leave to leave the kit editor.",
        CC.YELLOW
    ).highlightText(CC.GOLD, "/leave")
    val LEAVE_KIT_CREATOR: ChatMessage = ChatMessage(
        "Please type /leave to leave the kit creator.",
        CC.YELLOW
    ).highlightText(CC.GOLD, "/leave")
    val LEFT_QUEUE: ChatMessage = ChatMessage("You are no longer queued.", CC.AQUA).highlightText(
        CC.D_AQUA, "%queue%",
        "%gametype%"
    )
    val HEALTH: ChatMessage = ChatMessage("%player% now has %health% health remaining.", CC.AQUA).highlightText(
        CC.D_AQUA,
        "%health%", "%player%"
    )
    val NO_OPPONENT: ChatMessage = ChatMessage(
        "There was no opponent available for this round. You will play in the next round instead.",
        CC.AQUA
    )
    val SPECTATING_YOUR_MATCH: ChatMessage = ChatMessage("%player% is now spectating your match.", CC.AQUA)
        .highlightText(CC.D_AQUA, "%player%")
    val WON_TOURNAMENT: ChatMessage = ChatMessage("%player% has won the tournament.", CC.AQUA).highlightText(
        CC.D_AQUA,
        "%player%"
    )
    val SPECTATING_EVENT: ChatMessage = ChatMessage("You are now spectating the event.", CC.AQUA).highlightText(
        CC.D_AQUA,
        "the event"
    )
    val VIEW_ARENA: ChatMessage = ChatMessage("You are now viewing the arena.", CC.AQUA).highlightText(
        CC.D_AQUA,
        "the arena"
    )
    val WON_EVENT: ChatMessage =
        ChatMessage("%player% has won the event.", CC.AQUA).highlightText(CC.D_AQUA, "%player%")
    val ROUND_OVER: ChatMessage =
        ChatMessage("Round %round% is over. The next round will start in 5 seconds.", CC.AQUA)
            .highlightText(CC.D_AQUA, "%round%")
    val BEGINS_IN_SECONDS: ChatMessage = ChatMessage("The %type% will begin in %time% second(s).", CC.WHITE)
        .highlightText(CC.SECONDARY, "%type%", "%time% second(s)")
    val BEGINS_IN_MINUTES: ChatMessage = ChatMessage("The %type% will begin in %time% minutes(s).", CC.WHITE)
        .highlightText(CC.SECONDARY, "%type%", "%time% minutes(s)")
    val BATTLE_STARTED: ChatMessage = ChatMessage("The %type% has started.", CC.SECONDARY, true)
    val FOLLOWING: ChatMessage = ChatMessage("You are now following %player%.", CC.AQUA).highlightText(
        CC.D_AQUA,
        "%player%"
    )
    val POTS: ChatMessage = ChatMessage("You have %pots% health potions in your inventory.", CC.AQUA)
        .highlightText(CC.D_AQUA, "%pots%")
    val KILLED_BY_PLAYER: ChatMessage = ChatMessage("%victim% has been killed by %attacker%.", CC.AQUA)
        .highlightText(CC.D_AQUA, "%victim%", "%attacker%")
    val DIED: ChatMessage = ChatMessage("%victim% has died", CC.AQUA)
        .highlightText(CC.D_AQUA, "%victim%")
    val DUEL_REQUEST_SENT: ChatMessage = ChatMessage(
        "You have send a duel request to %player%. They have 30 seconds to accept.", CC.AQUA
    )
        .highlightText(CC.D_AQUA, "%player%")
    val SPECTATING: ChatMessage = ChatMessage("You are now spectating %player%.", CC.AQUA).highlightText(
        CC.D_AQUA,
        "%player%"
    )
    val CAN_NOT_BROADCAST: ChatMessage = ChatMessage(
        "The message to join the party can only be broadcasted once every 20 seconds.", CC.AQUA
    )
    val JOINED_PARTY: ChatMessage = ChatMessage("%player% has joined the party.", CC.GREEN).highlightText(
        CC.D_GREEN,
        "%player%"
    )
    val LEFT_PARTY: ChatMessage =
        ChatMessage("%player% has left the party.", CC.RED).highlightText(CC.D_RED, "%player%")
    val JOINED_TOURNAMENT: ChatMessage = ChatMessage("%player% has joined the tourmament.", CC.GREEN)
        .highlightText(CC.D_GREEN, "%player%")
    val LEFT_TOURNAMENT: ChatMessage = ChatMessage("%player% has left the tournament.", CC.RED).highlightText(
        CC.D_RED,
        "%player%"
    )
    val JOINED_EVENT: ChatMessage = ChatMessage("%player% has joined the event.", CC.GREEN).highlightText(
        CC.D_GREEN,
        "%player%"
    )
    val LEFT_EVENT: ChatMessage =
        ChatMessage("%player% has left the event.", CC.RED).highlightText(CC.D_RED, "%player%")
    val PARTY_REQUEST_SENT: ChatMessage = ChatMessage(
        "You have send a party invite to %player%. They have 30 seconds to accept.", CC.AQUA
    )
        .highlightText(CC.D_AQUA, "%player%")
    val TIME_SET_DAY: ChatMessage = ChatMessage("Your time has been set to day.", CC.YELLOW)
        .highlightText(CC.GOLD, "day")
    val TIME_SET_NIGHT: ChatMessage = ChatMessage("Your time has been set to night.", CC.YELLOW)
        .highlightText(CC.GOLD, "night")

    val DUEL_REQUEST_RECIEVED: ClickableChatMessage = ClickableChatMessage(
        "You have recieved a duel request from %player%. [Click To Accept]",
        CC.AQUA
    )
        .highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Accept]")
    val CONTEST_JOIN: ClickableChatMessage = ClickableChatMessage(
        "(CLICK TO JOIN)",
        CC.GREEN, true
    )
    val TOURNAMENT_BROADCAST: ChatMessage = ChatMessage("Tournament", CC.SECONDARY, true)
    val EVENT_BROADCAST: ChatMessage = ChatMessage("Event", CC.SECONDARY, true)

    val CONTEST_HOST = ChatMessage("✱ Host: %host%", CC.WHITE).highlightText(CC.YELLOW, "%host%")
    val CONTEST_MODE =
        ChatMessage("✱ Mode: %mode%", CC.WHITE).highlightText(CC.YELLOW, "%mode%")
    val CONTEST_PLAYERS = ChatMessage("✱ Players: %players%", CC.WHITE).highlightText(CC.YELLOW, "%players%")
    val CONTEST_REWARD = ChatMessage("✱ Reward: %rank% Rank", CC.WHITE).highlightText(CC.YELLOW, "%rank% Rank")
    val CONTEST_STARTS_IN = ChatMessage("Starts in %time%...", CC.ACCENT)

    val PARTY_REQUEST_RECIEVED: ClickableChatMessage = ClickableChatMessage(
        "You have recieved a party invite from %player%. [Click To Accept]",
        CC.AQUA
    )
        .highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Accept]")
    val BROADCAST_PARTY_OPEN: ClickableChatMessage = ClickableChatMessage(
        "%player% has opened their party. [Click To Join]",
        CC.AQUA
    )
        .highlightText(CC.D_AQUA, "%player%").highlightText(CC.GREEN, "[Click To Join]")

    // Title
    val CONFIG_COMMANDS: ChatMessage = ChatMessage(
        "Config Commands", CC.PRIMARY,
        false
    )
    val ARENA_COMMANDS: ChatMessage = ChatMessage(
        "Arena Commands", CC.PRIMARY,
        false
    )
    val CATEGORY_COMMANDS: ChatMessage = ChatMessage(
        "Category Commands", CC.PRIMARY,
        false
    )
    val GAMETYPE_COMMANDS: ChatMessage = ChatMessage(
        "Gametype Commands", CC.PRIMARY,
        false
    )
    val QUEUETYPE_COMMANDS: ChatMessage = ChatMessage(
        "Queuetype Commands", CC.PRIMARY,
        false
    )
    val KIT_EDITOR_COMMANDS: ChatMessage = ChatMessage(
        "Kit Editor Commands", CC.PRIMARY,
        false
    )
    val PARTIES_COMMANDS: ChatMessage = ChatMessage(
        "Parties Commands", CC.PRIMARY,
        false
    )
    val SETTINGS_COMMANDS: ChatMessage = ChatMessage(
        "Settings Config Commands", CC.PRIMARY,
        false
    )
    val SPECTATE_COMMANDS: ChatMessage = ChatMessage(
        "Spectate Config Commands", CC.PRIMARY,
        false
    )
    val LEADERBOARD_COMMANDS: ChatMessage = ChatMessage(
        "Leaderboard Config Commands", CC.PRIMARY,
        false
    )

    // Arena Command
    val ARENA_CREATE: ChatMessage = ListElementMessage(
        "/arena create <Name>",
        CC.SECONDARY
    )
    val ARENA_SPAWN: ChatMessage = ListElementMessage(
        "/arena spawn <Arena> <1/2/Waiting>",
        CC.SECONDARY
    )
    val ARENA_DISPLAY: ChatMessage = ListElementMessage(
        "/arena setdisplay <Arena> <&{Colour}>",
        CC.SECONDARY
    )
    val ARENA_LIST: ChatMessage = ListElementMessage(
        "/arena list",
        CC.SECONDARY
    )
    val ARENA_TP: ChatMessage = ListElementMessage(
        "/arena tp <Arena>",
        CC.SECONDARY
    )
    val ARENA_DELETE: ChatMessage = ListElementMessage(
        "/arena delete <Name>",
        CC.SECONDARY
    )

    // Category Command
    val CATEGORY_CREATE: ChatMessage = ListElementMessage(
        "/category create <Name>",
        CC.SECONDARY
    )
    val CATEGORY_DISPLAY: ChatMessage = ListElementMessage(
        "/category setdisplay <Category> <DisplayName>",
        CC.SECONDARY
    )
    val CATEGORY_QUEUE: ChatMessage = ListElementMessage(
        "/category queue <Category> <Queuetype> <Slot/False>",
        CC.SECONDARY
    )
    val CATEGORY_LIST: ChatMessage = ListElementMessage(
        "/category list",
        CC.SECONDARY
    )
    val CATEGORY_ADD: ChatMessage = ListElementMessage(
        "/category add <Category> <Gametype>",
        CC.SECONDARY
    )
    val CATEGORY_REMOVE: ChatMessage = ListElementMessage(
        "/category remove <Category> <Gametype>",
        CC.SECONDARY
    )
    val CATEGORY_DELETE: ChatMessage = ListElementMessage(
        "/category delete <Name>",
        CC.SECONDARY
    )

    // Gametype Command
    val GAMETYPE_CREATE: ChatMessage = ListElementMessage(
        "/gametype create <Name>",
        CC.SECONDARY
    )
    val GAMETYPE_LOAD_KIT: ChatMessage = ListElementMessage(
        "/gametype loadkit <Name>",
        CC.SECONDARY
    )
    val GAMETYPE_KIT: ChatMessage = ListElementMessage(
        "/gametype kit <Gametype>",
        CC.SECONDARY
    )
    val GAMETYPE_DISPLAY: ChatMessage = ListElementMessage(
        "/gametype setdisplay <Gametype> <DisplayName>",
        CC.SECONDARY
    )
    val GAMETYPE_DAMAGE_TICKS: ChatMessage = ListElementMessage(
        "/gametype nodamageticks <Gametype> <Ticks>",
        CC.SECONDARY
    )
    val GAMETYPE_BUILD_LIMIT: ChatMessage = ListElementMessage(
        "/gametype buildlimit <Gametype> <Height>",
        CC.SECONDARY
    )
    val GAMETYPE_REGEN: ChatMessage = ListElementMessage(
        "/gametype regen <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_BOTS: ChatMessage = ListElementMessage(
        "/gametype bots <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_GRIEFING: ChatMessage = ListElementMessage(
        "/gametype griefing <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_QUEUE: ChatMessage = ListElementMessage(
        "/gametype queue <Gametype> <Queuetype> <Slot/False>",
        CC.SECONDARY
    )
    val GAMETYPE_LIST: ChatMessage = ListElementMessage(
        "/gametype list",
        CC.SECONDARY
    )
    val GAMETYPE_BUILD: ChatMessage = ListElementMessage(
        "/gametype build <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_DEADLY_WATER: ChatMessage = ListElementMessage(
        "/gametype deadlywater <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_LOOTING: ChatMessage = ListElementMessage(
        "/gametype looting <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_DAMAGE: ChatMessage = ListElementMessage(
        "/gametype damage <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_HUNGER: ChatMessage = ListElementMessage(
        "/gametype hunger <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_BOXING: ChatMessage = ListElementMessage(
        "/gametype boxing <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_EPEARL: ChatMessage = ListElementMessage(
        "/gametype epearl <Gametype> <Time(s)>",
        CC.SECONDARY
    )
    val GAMETYPE_ARENA: ChatMessage = ListElementMessage(
        "/gametype arena <Gametype>",
        CC.SECONDARY
    )
    val GAMETYPE_EVENT: ChatMessage = ListElementMessage(
        "/gametype event <Gametype> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_EVENT_ARENA: ChatMessage = ListElementMessage(
        "/gametype seteventarena <Gametype> <Arena>",
        CC.SECONDARY
    )
    val GAMETYPE_ARENA_FOR_ALL: ChatMessage = ListElementMessage(
        "/gametype enablearenaforall <Arena> <True/False>",
        CC.SECONDARY
    )
    val GAMETYPE_DELETE: ChatMessage = ListElementMessage(
        "/gametype delete <Gametype>",
        CC.SECONDARY
    )

    // Kit Editor Command
    val KIT_EDITOR_ENABLE: ChatMessage = ListElementMessage(
        "/kiteditor enable <True/False>",
        CC.SECONDARY
    )
    val KIT_EDITOR_DISPLAY: ChatMessage = ListElementMessage(
        "/kiteditor setdisplay <DisplayName>",
        CC.SECONDARY
    )
    val KIT_EDITOR_SLOT: ChatMessage = ListElementMessage(
        "/kiteditor slot <Slot>",
        CC.SECONDARY
    )
    val KIT_EDITOR_LOCATION: ChatMessage = ListElementMessage(
        "/kiteditor setlocation",
        CC.SECONDARY
    )

    // List Config Commands
    val QUEUETYPE: ChatMessage = ListElementMessage(
        "/queuetype",
        CC.SECONDARY
    )
    val GAMETYPE: ChatMessage = ListElementMessage(
        "/gametype",
        CC.SECONDARY
    )
    val CATEGORY: ChatMessage = ListElementMessage(
        "/category",
        CC.SECONDARY
    )
    val EVENTS: ChatMessage = ListElementMessage(
        "/events",
        CC.SECONDARY
    )
    val ARENA: ChatMessage = ListElementMessage(
        "/arena",
        CC.SECONDARY
    )
    val KIT_EDITOR: ChatMessage = ListElementMessage("/kiteditor", CC.SECONDARY)
    val PARTIES: ChatMessage = ListElementMessage("/parties", CC.SECONDARY)
    val LOBBY: ChatMessage = ListElementMessage("/lobby", CC.SECONDARY)
    val SETTINGS_CONFIG: ChatMessage = ListElementMessage("/settingsconfig", CC.SECONDARY)

    // Parties Command
    val PARTIES_ENABLE: ChatMessage = ListElementMessage(
        "/parties enable <True/False>",
        CC.SECONDARY
    )
    val PARTIES_DISPLAY: ChatMessage = ListElementMessage(
        "/parties setdisplay <DisplayName>",
        CC.SECONDARY
    )
    val PARTIES_SLOT: ChatMessage = ListElementMessage(
        "/parties slot <Slot>",
        CC.SECONDARY
    )

    // Queuetype Command
    val QUEUETYPE_CREATE: ChatMessage = ListElementMessage(
        "/queuetype create <Name>",
        CC.SECONDARY
    )
    val QUEUETYPE_DISPLAY: ChatMessage = ListElementMessage(
        "/queuetype setdisplay <Queuetype> <DisplayName>",
        CC.SECONDARY
    )
    val QUEUETYPE_RANKED: ChatMessage = ListElementMessage(
        "/queuetype ranked <Queuetype> <True/False>",
        CC.SECONDARY
    )
    val QUEUETYPE_COMMUNITY: ChatMessage = ListElementMessage(
        "/queuetype community <Queuetype> <True/False>",
        CC.SECONDARY
    )
    val QUEUETYPE_UNRANKED: ChatMessage = ListElementMessage(
        "/queuetype unranked <Queuetype> <True/False>",
        CC.SECONDARY
    )
    val QUEUETYPE_BOTS: ChatMessage = ListElementMessage(
        "/queuetype bots <Queuetype> <True/False>",
        CC.SECONDARY
    )
    val QUEUETYPE_SLOT: ChatMessage = ListElementMessage(
        "/queuetype slot <Queuetype> <Slot>",
        CC.SECONDARY
    )
    val QUEUETYPE_LIST: ChatMessage = ListElementMessage(
        "/queuetype list",
        CC.SECONDARY
    )
    val QUEUETYPE_KB: ChatMessage = ListElementMessage(
        "/queuetype kb <Queuetype> <KnockbackProfile>",
        CC.SECONDARY
    )
    val QUEUETYPE_ARENA: ChatMessage = ListElementMessage(
        "/queuetype arena <Queuetype>",
        CC.SECONDARY
    )
    val QUEUETYPE_DELETE: ChatMessage = ListElementMessage(
        "/queuetype delete <Queuetype>",
        CC.SECONDARY
    )

    // Settings Config Command
    val SETTINGS_ENABLE: ChatMessage = ListElementMessage(
        "/settingsconfig enable <True/False>",
        CC.SECONDARY
    )
    val SETTINGS_DISPLAY: ChatMessage = ListElementMessage(
        "/settingsconfig setdisplay <DisplayName>",
        CC.SECONDARY
    )
    val SETTINGS_SLOT: ChatMessage = ListElementMessage(
        "/settingsconfig slot <Slot>",
        CC.SECONDARY
    )

    // Spectate Config Command
    val SPECTATE_ENABLE: ChatMessage = ListElementMessage(
        "/spectateconfig enable <True/False>",
        CC.SECONDARY
    )
    val SPECTATE_DISPLAY: ChatMessage = ListElementMessage(
        "/spectateconfig setdisplay <DisplayName>",
        CC.SECONDARY
    )
    val SPECTATE_SLOT: ChatMessage = ListElementMessage(
        "/spectateconfig slot <Slot>",
        CC.SECONDARY
    )

    // Leaderboard Config Command
    val LEADERBOARD_ENABLE: ChatMessage = ListElementMessage(
        "/leaderboardconfig enable <True/False>",
        CC.SECONDARY
    )
    val LEADERBOARD_DISPLAY: ChatMessage = ListElementMessage(
        "/leaderboardconfig setdisplay <DisplayName>",
        CC.SECONDARY
    )
    val LEADERBOARD_SLOT: ChatMessage = ListElementMessage(
        "/leaderboardconfig slot <Slot>",
        CC.SECONDARY
    )

    // Party Command
    val PARTY_CREATE: ChatMessage = ListElementMessage("/party create", CC.SECONDARY)
    val PARTY_INVITE: ChatMessage = ListElementMessage("/party invite <Player>", CC.SECONDARY)
    val PARTY_OPEN: ChatMessage = ListElementMessage("/party open", CC.SECONDARY)
    val PARTY_LIST: ChatMessage = ListElementMessage("/party list", CC.SECONDARY)
    val PARTY_JOIN: ChatMessage = ListElementMessage("/party join <PartyLeader>", CC.SECONDARY)
    val PARTY_DUEL: ChatMessage = ListElementMessage("/duel <PartyLeader>", CC.SECONDARY)
    val PARTY_ACCEPT: ChatMessage = ListElementMessage("/party accept <PartyLeader>", CC.SECONDARY)
    val PARTY_LEAVE: ChatMessage = ListElementMessage("/party leave", CC.SECONDARY)
    val PARTY_DISBAND: ChatMessage = ListElementMessage("/party disband", CC.SECONDARY)

    // Events
    val CLICK_TO_ACCEPT: HoverEvent = HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        ComponentBuilder(CC.GREEN + "Click To Accept")
            .create()
    )
    val CLICK_TO_JOIN: HoverEvent = HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        ComponentBuilder(CC.GREEN + "Click To Join").create()
    )
}
