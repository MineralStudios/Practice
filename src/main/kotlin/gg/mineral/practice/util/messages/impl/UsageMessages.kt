package gg.mineral.practice.util.messages.impl

import gg.mineral.practice.util.messages.UsageMessage

object UsageMessages {
    // Arena Command
    val ARENA_CREATE: UsageMessage = UsageMessage("/arena create <Name>")
    val ARENA_SPAWN: UsageMessage = UsageMessage("/arena spawn <Arena> <1/2/Waiting>")
    val ARENA_DISPLAY: UsageMessage = UsageMessage("/arena setdisplay <Arena> <DisplayName>")
    val ARENA_TP: UsageMessage = UsageMessage("/arena tp <Arena>")
    val ARENA_DELETE: UsageMessage = UsageMessage("/arena delete <Arena>")

    // Category Command
    val CATEGORY_CREATE: UsageMessage = UsageMessage("/category create <Name>")
    val CATEGORY_DISPLAY: UsageMessage = UsageMessage(
        "/category setdisplay <Category> <DisplayName>"
    )
    val CATEGORY_QUEUE: UsageMessage = UsageMessage(
        "/category queue <Category> <Queuetype> <Slot/False>"
    )
    val CATEGORY_ADD: UsageMessage = UsageMessage("/category add <Category> <Gametype>")
    val CATEGORY_REMOVE: UsageMessage = UsageMessage("/category remove <Category> <Gametype>")
    val CATEGORY_DELETE: UsageMessage = UsageMessage("/category delete <Category>")

    // Gametype Command
    val GAMETYPE_CREATE: UsageMessage = UsageMessage("/gametype create <Name>")
    val GAMETYPE_LOAD_KIT: UsageMessage = UsageMessage("/gametype loadkit <Name>")
    val GAMETYPE_KIT: UsageMessage = UsageMessage("/gametype kit <Gametype>")
    val GAMETYPE_DISPLAY: UsageMessage = UsageMessage(
        "/gametype setdisplay <Gametype> <DisplayName>"
    )
    val GAMETYPE_DAMAGE_TICKS: UsageMessage = UsageMessage(
        "/gametype nodamageticks <Gametype> <Ticks>"
    )
    val GAMETYPE_BUILD_LIMIT: UsageMessage = UsageMessage(
        "/gametype buildlimit <Gametype> <Height>"
    )
    val GAMETYPE_REGEN: UsageMessage = UsageMessage("/gametype regen <Gametype> <True/False>")
    val GAMETYPE_BOTS: UsageMessage = UsageMessage("/gametype bots <Gametype> <True/False>")
    val GAMETYPE_GRIEFING: UsageMessage = UsageMessage(
        "/gametype griefing <Gametype> <True/False>"
    )
    val GAMETYPE_QUEUE: UsageMessage = UsageMessage(
        "/gametype queue <Gametype> <Queuetype> <Slot/False>"
    )
    val GAMETYPE_BUILD: UsageMessage = UsageMessage("/gametype build <Gametype> <True/False>")
    val GAMETYPE_DEADLY_WATER: UsageMessage = UsageMessage(
        "/gametype deadlywater <Gametype> <True/False>"
    )
    val GAMETYPE_LOOTING: UsageMessage = UsageMessage(
        "/gametype looting <Gametype> <True/False>"
    )
    val GAMETYPE_DAMAGE: UsageMessage = UsageMessage("/gametype damage <Gametype> <True/False>")
    val GAMETYPE_HUNGER: UsageMessage = UsageMessage("/gametype hunger <Gametype> <True/False>")
    val GAMETYPE_BOXING: UsageMessage = UsageMessage("/gametype boxing <Gametype> <True/False>")
    val GAMETYPE_EPEARL: UsageMessage = UsageMessage("/gametype epearl <Gametype> <Time(s)>")
    val GAMETYPE_ARENA: UsageMessage = UsageMessage(
        "/gametype arena <Gametype> <Arena> <True/False>"
    )
    val GAMETYPE_EVENT: UsageMessage = UsageMessage("/gametype event <Gametype> <True/False>")
    val GAMETYPE_EVENT_ARENA: UsageMessage = UsageMessage(
        "/gametype seteventarena <Gametype> <Arena>"
    )
    val GAMETYPE_ARENA_FOR_ALL: UsageMessage = UsageMessage(
        "/gametype enablearenaforall <Arena> <True/False>"
    )
    val GAMETYPE_DELETE: UsageMessage = UsageMessage("/gametype delete <Gametype>")

    // Kit Editor Command
    val KIT_EDITOR_ENABLE: UsageMessage = UsageMessage("/kiteditor enable <True/False>")
    val KIT_EDITOR_DISPLAY: UsageMessage = UsageMessage("/kiteditor setdisplay <DisplayName>")
    val KIT_EDITOR_SLOT: UsageMessage = UsageMessage("/kiteditor slot <Slot>")

    // Parties Command
    val PARTIES_ENABLE: UsageMessage = UsageMessage("/parties enable <True/False>")
    val PARTIES_DISPLAY: UsageMessage = UsageMessage("/parties setdisplay <DisplayName>")
    val PARTIES_SLOT: UsageMessage = UsageMessage("/parties slot <Slot>")

    // Queuetype Command
    val QUEUETYPE_CREATE: UsageMessage = UsageMessage("/queuetype create <Name>")
    val QUEUETYPE_DISPLAY: UsageMessage = UsageMessage(
        "/queuetype setdisplay <Queuetype> <DisplayName>"
    )
    val QUEUETYPE_RANKED: UsageMessage = UsageMessage(
        "/queuetype ranked <Queuetype> <True/False>"
    )
    val QUEUETYPE_COMMUNITY: UsageMessage = UsageMessage(
        "/queuetype community <Queuetype> <True/False>"
    )
    val QUEUETYPE_UNRANKED: UsageMessage = UsageMessage(
        "/queuetype unranked <Queuetype> <True/False>"
    )
    val QUEUETYPE_SLOT: UsageMessage = UsageMessage("/queuetype slot <Queuetype> <Slot>")
    val QUEUETYPE_KB: UsageMessage = UsageMessage(
        "/queuetype kb <Queuetype> <KnockbackProfile>"
    )
    val QUEUETYPE_BOTS: UsageMessage = UsageMessage(
        "/queuetype bots <Queuetype> <True/False>"
    )
    val QUEUETYPE_ARENA: UsageMessage = UsageMessage(
        "/queuetype arena <Queuetype>"
    )
    val QUEUETYPE_DELETE: UsageMessage = UsageMessage("/queuetype delete <Queuetype>")

    // Settings Config Command
    val SETTINGS_ENABLE: UsageMessage = UsageMessage("/settingsconfig enable <True/False>")
    val SETTINGS_DISPLAY: UsageMessage = UsageMessage(
        "/settingsconfig setdisplay <DisplayName>"
    )
    val SETTINGS_SLOT: UsageMessage = UsageMessage("/settingsconfig slot <Slot>")

    // Spectate Config Command
    val SPECTATE_ENABLE: UsageMessage = UsageMessage("/spectateconfig enable <True/False>")
    val SPECTATE_DISPLAY: UsageMessage = UsageMessage(
        "/spectateconfig setdisplay <DisplayName>"
    )
    val SPECTATE_SLOT: UsageMessage = UsageMessage("/spectateconfig slot <Slot>")

    // Leaderboard Config Command
    val LEADERBOARD_ENABLE: UsageMessage = UsageMessage("/leaderboardconfig enable <True/False>")
    val LEADERBOARD_DISPLAY: UsageMessage = UsageMessage(
        "/leaderboardconfig setdisplay <DisplayName>"
    )
    val LEADERBOARD_SLOT: UsageMessage = UsageMessage("/leaderboardconfig slot <Slot>")

    // Accept Command
    val ACCEPT: UsageMessage = UsageMessage("/accept <Player>")

    // Duel Command
    val DUEL: UsageMessage = UsageMessage("/duel <Player>")

    // Party Command
    val PARTY_INVITE: UsageMessage = UsageMessage("/party invite <Name>")
    val PARTY_JOIN: UsageMessage = UsageMessage("/party join <Name>")
    val PARTY_ACCEPT: UsageMessage = UsageMessage("/party accept <Name>")

    // Follow Command
    val FOLLOW: UsageMessage = UsageMessage("/follow <Player>")

    // View Inventory Command
    val VIEW_INV: UsageMessage = UsageMessage("/viewinventory <Player>")

    // Join Command
    val JOIN: UsageMessage = UsageMessage("/join <Name>")
}
