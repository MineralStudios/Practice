package gg.mineral.practice.util.items

import gg.mineral.practice.util.messages.CC
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.inventory.ItemStack

object ItemStacks {
    // Item Stacks
    val STOP_FOLLOWING: ItemStack = ItemBuilder(ItemStack(351, 1, 1.toShort()))
        .name(CC.SECONDARY + CC.B + "Stop Following").lore(CC.ACCENT + "Right click to stop following.")
        .build()
    val STOP_SPECTATING: ItemStack = ItemBuilder(ItemStack(351, 1, 1.toShort()))
        .name(CC.SECONDARY + CC.B + "Stop Spectating")
        .lore(CC.ACCENT + "Right click to stop spectating.").build()
    val WAIT_TO_LEAVE: ItemStack = ItemBuilder(ItemStack(351, 1, 14.toShort()))
        .name(CC.SECONDARY + CC.B + "Please Wait")
        .lore(CC.ACCENT + "You must wait before leaving.").build()
    val LEAVE_TOURNAMENT: ItemStack = ItemBuilder(ItemStack(351, 1, 1.toShort()))
        .name(CC.SECONDARY + CC.B + "Leave Tournament")
        .lore(CC.ACCENT + "Right click to leave.").build()
    val LEAVE_EVENT: ItemStack = ItemBuilder(ItemStack(351, 1, 1.toShort()))
        .name(CC.SECONDARY + CC.B + "Leave Event")
        .lore(CC.ACCENT + "Right click to leave.").build()
    val LEAVE_PARTY: ItemStack = ItemBuilder(ItemStack(351, 1, 1.toShort()))
        .name(CC.SECONDARY + CC.B + "Leave Party")
        .lore(CC.ACCENT + "Right click to leave.").build()
    val LEAVE_QUEUE: ItemStack = ItemBuilder(ItemStack(351, 1, 1.toShort()))
        .name(CC.SECONDARY + CC.B + "Leave Queue")
        .lore(CC.ACCENT + "Right click to leave.").build()
    val QUEUE_MANAGER: ItemStack = ItemBuilder(Material.ITEM_FRAME).name(CC.SECONDARY + CC.B + "Queue Manager")
        .lore(CC.ACCENT + "Right click to manage queued game types.")
        .build()
    val QUEUE: ItemStack = ItemBuilder(Material.COMPASS).name(CC.SECONDARY + CC.B + "Queue")
        .lore(CC.ACCENT + "Right click to queue.").build()
    val QUEUE_AGAIN: ItemStack = ItemBuilder(Material.PAPER).name(CC.SECONDARY + CC.B + "Queue Again")
        .lore(CC.ACCENT + "Right click to queue again.").build()
    val LIST_PLAYERS: ItemStack = ItemBuilder(Material.PAPER).name(CC.SECONDARY + CC.B + "List Players")
        .lore(CC.ACCENT + "Right click to list players.")
        .build()
    val DUEL: ItemStack = ItemBuilder(Material.WOOD_AXE).name(CC.SECONDARY + CC.B + "Duel")
        .lore(CC.ACCENT + "Right click to duel.").build()
    val PARTY_SPLIT: ItemStack = ItemBuilder(Material.GOLD_AXE).name(CC.SECONDARY + CC.B + "Party Split")
        .lore(CC.ACCENT + "Right click to start party split.")
        .build()
    val OPEN_PARTY: ItemStack = ItemBuilder(Material.SKULL_ITEM).name(CC.SECONDARY + CC.B + "Open Party")
        .lore(CC.ACCENT + "Right click to open party.")
        .build()
    val NO_HEALTH: ItemStack = ItemBuilder(Material.SKULL_ITEM).name(CC.RED + CC.B + "Dead").build()
    val VIEW_OPPONENT_INVENTORY: ItemStack = ItemBuilder(Material.LEVER)
        .name(CC.SECONDARY + CC.B + "View Opponent Inventory")
        .lore(CC.ACCENT + "Click to view.")
        .build()
    val SUBMIT: ItemStack = ItemBuilder(Material.STICK).name(CC.SECONDARY + CC.B + "Submit")
        .lore(CC.ACCENT + "Click to confirm settings.").build()
    val RESET_SETTINGS: ItemStack = ItemBuilder(Material.PAPER).name(CC.SECONDARY + CC.B + "Reset Settings")
        .lore(CC.ACCENT + "Click to reset settings.")
        .build()
    val SAVE_KIT: ItemStack = ItemBuilder(Material.ENCHANTED_BOOK)
        .name(CC.SECONDARY + CC.B + "Save Kit")
        .lore(
            (CC.WHITE + "Saves a " + CC.SECONDARY + "kit"
                    + CC.WHITE + " to this hotbar slot."),
            " ",
            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to save."
        )
        .build()
    val DELETE_KIT: ItemStack = ItemBuilder(ItemStack(351, 1, 1.toShort()))
        .name(CC.SECONDARY + CC.B + "Delete Kit")
        .lore(
            (CC.WHITE + "Deletes a " + CC.SECONDARY + "kit"
                    + CC.WHITE + " from this hotbar slot."),
            " ",
            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to delete."
        )
        .build()
    val CHOOSE_EXISTING_KIT: ItemStack = ItemBuilder(Material.LEATHER_CHESTPLATE)
        .name(CC.SECONDARY + CC.B + "Choose Existing Kit")
        .lore(
            (CC.WHITE + "Create a " + CC.SECONDARY + "custom kit"
                    + CC.WHITE + "."),
            " ",
            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to create kit."
        )
        .build()
    val CREATE_CUSTOM_KNOCKBACK: ItemStack = ItemBuilder(Material.STONE_SWORD)
        .name(CC.SECONDARY + CC.B + "Create Custom Knockback")
        .lore(
            (CC.WHITE + "Create a " + CC.SECONDARY + "custom knockback"
                    + CC.WHITE + "."),
            " ",
            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to create knockback."
        )
        .build()
    val CHOOSE_CUSTOM_KIT: ItemStack = ItemBuilder(Material.GOLD_CHESTPLATE)
        .name(CC.SECONDARY + CC.B + "Create Custom Kit")
        .lore(
            (CC.WHITE + "Choose a " + CC.SECONDARY + "preconfigured kit"
                    + CC.WHITE + "."),
            " ",
            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select kit."
        )
        .build()
    val SIMPLE_MODE: ItemStack = ItemBuilder(Material.GREEN_RECORD).name(CC.SECONDARY + CC.B + "Simple Mode")
        .lore(
            CC.WHITE + "A more " + CC.SECONDARY + "simple and familiar", CC.WHITE
                    + "duel interface.",
            " ",
            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select."
        )
        .build()
    val ADVANCED_MODE: ItemStack = ItemBuilder(Material.GOLD_RECORD)
        .name(CC.SECONDARY + CC.B + "Advanced Mode")
        .lore(
            (CC.WHITE + "A more " + CC.SECONDARY + "advanced" + CC.WHITE
                    + " duel interface"), CC.WHITE + "with many options.",
            " ",
            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select."
        )
        .build()
    val AIR: ItemStack = ItemStack(Material.AIR)
    val BLACK_STAINED_GLASS: ItemStack = ItemBuilder(
        ItemStack(
            Material.STAINED_GLASS_PANE, 1,
            DyeColor.BLACK.data.toShort()
        )
    )
        .name(null).build()
    val DEFAULT_ARENA_DISPLAY_ITEM: ItemStack = ItemStack(Material.WOOL)
    val DEFAULT_CATEGORY_DISPLAY_ITEM: ItemStack = ItemStack(Material.DIAMOND_SWORD)
    val DEFAULT_GAMETYPE_DISPLAY_ITEM: ItemStack = ItemStack(Material.DIAMOND_SWORD)
    val EMPTY_BOWL: ItemStack = ItemStack(Material.BOWL)
    val DEFAULT_KIT_EDITOR_DISPLAY_ITEM: ItemStack = ItemStack(Material.BOOK)
    val DEFAULT_OPTIONS_DISPLAY_ITEM: ItemStack = ItemStack(Material.COMPASS)
    val DEFAULT_SPECTATE_DISPLAY_ITEM: ItemStack = ItemStack(Material.HOPPER)
    val DEFAULT_LEADERBOARD_DISPLAY_ITEM: ItemStack = ItemStack(
        Material.SKULL_ITEM, 1,
        SkullType.PLAYER.ordinal.toShort()
    )
    val DEFAULT_PARTY_DISPLAY_ITEM: ItemStack = ItemStack(Material.NETHER_STAR)
    val DEFAULT_QUEUETYPE_DISPLAY_ITEM: ItemStack = ItemStack(Material.DIAMOND_SWORD)
    val WOOD_AXE: ItemStack = ItemStack(Material.WOOD_AXE)
    val APPLY: ItemStack = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.BLUE.data.toShort())).name(" ")
        .lore(CC.GOLD + CC.B + "Click To Apply Changes", " ").build()
    val DESELECT_ALL: ItemStack = ItemBuilder(Material.REDSTONE_BLOCK).name(" ")
        .lore(CC.RED + CC.B + "Deselect All", " ").build()
    val SELECT_ALL: ItemStack = ItemBuilder(Material.LAPIS_BLOCK).name(" ")
        .lore(CC.GREEN + CC.B + "Select All", " ").build()
    val CANCEL: ItemStack = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.RED.data.toShort())).name(" ")
        .lore(CC.RED + CC.B + "Click To Cancel Changes", " ").build()
    val RANDOM_QUEUE: ItemStack = ItemBuilder(Material.PAPER)
        .name(CC.SECONDARY + CC.B + "Random Queue")
        .lore(
            CC.WHITE + "Adds you to a " + CC.BLUE + "random" + CC.WHITE + " queue.",
            " ", CC.BOARD_SEPARATOR,
            CC.ACCENT + "Click to be added to a random queue."
        )
        .build()
    val EASY: ItemStack = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.GREEN.data.toShort())).name("Easy")
        .build()
    val MEDIUM: ItemStack = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.YELLOW.data.toShort()))
        .name("Medium")
        .build()
    val HARD: ItemStack = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.ORANGE.data.toShort())).name("Hard")
        .build()
    val EXPERT: ItemStack = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.RED.data.toShort())).name("Expert")
        .build()
    val HACKER: ItemStack = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.PURPLE.data.toShort()))
        .name("Hacker")
        .build()
    val BOT_QUEUE_DISABLED: ItemStack = ItemBuilder(ItemStack(351, 1, DyeColor.GRAY.dyeData.toShort()))
        .name(CC.SECONDARY + CC.B + "Bot Queue")
        .lore(
            (CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "bot" + CC.WHITE
                    + " match."), " ",
            CC.WHITE + "Currently:",
            CC.RED + "Disabled",
            " ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle bots."
        )
        .build()
    val BOT_QUEUE_ENABLED: ItemStack = ItemBuilder(ItemStack(351, 1, DyeColor.LIME.dyeData.toShort()))
        .name(CC.SECONDARY + CC.B + "Bot Queue")
        .lore(
            (CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "bot" + CC.WHITE
                    + " match."), " ",
            CC.WHITE + "Currently:",
            CC.GREEN + "Enabled",
            " ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle bots."
        )
        .build()
    val BACK: ItemStack = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.RED.data.toShort()))
        .name(CC.SECONDARY + CC.B + "Back").lore(CC.ACCENT + "Click to go back.")
        .build()

    // Item Builders
    val LOAD_KIT: ItemBuilder = ItemBuilder(Material.ENCHANTED_BOOK)
    val TEAMFIGHT: ItemBuilder = ItemBuilder(Material.ITEM_FRAME)
        .name(CC.SECONDARY + CC.B + "Team Fights")
    val BOT_SETTINGS: ItemBuilder = ItemBuilder(Material.GOLD_HELMET)
        .name(CC.SECONDARY + CC.B + "Bot Settings")
    val PREMADE_DIFFICULTY: ItemBuilder = ItemBuilder(Material.PAPER)
        .name(CC.SECONDARY + CC.B + "Premade Difficulty")
    val CLICK_TO_APPLY_CHANGES: ItemBuilder = ItemBuilder(Material.STONE_SWORD)
        .lore(CC.ACCENT + "Click To Apply Changes")
    val INVENTORY_STATS: ItemBuilder = ItemBuilder(Material.ENDER_CHEST)
    val SELECT_KIT: ItemBuilder = ItemBuilder(Material.DIAMOND_CHESTPLATE)
        .name(CC.SECONDARY + CC.B + "Select Kit")
    val CHANGE_KNOCKBACK: ItemBuilder = ItemBuilder(Material.STICK)
        .name(CC.SECONDARY + CC.B + "Change Knockback")
    val HIT_DELAY: ItemBuilder = ItemBuilder(Material.WATCH)
        .name(CC.SECONDARY + CC.B + "Hit Delay")
    val TOGGLE_HUNGER: ItemBuilder = ItemBuilder(Material.COOKED_BEEF)
        .name(CC.SECONDARY + CC.B + "Toggle Hunger")
    val DEADLY_WATER: ItemBuilder = ItemBuilder(Material.BLAZE_ROD)
        .name(CC.SECONDARY + CC.B + "Deadly Water")
    val TOGGLE_BUILD: ItemBuilder = ItemBuilder(Material.BRICK)
        .name(CC.SECONDARY + CC.B + "Toggle Build")
    val TOGGLE_DAMAGE: ItemBuilder = ItemBuilder(Material.DIAMOND_AXE)
        .name(CC.SECONDARY + CC.B + "Toggle Damage")
    val TOGGLE_GRIEFING: ItemBuilder = ItemBuilder(Material.TNT)
        .name(CC.SECONDARY + CC.B + "Toggle Griefing")
    val PEARL_COOLDOWN: ItemBuilder = ItemBuilder(Material.ENDER_PEARL)
        .name(CC.SECONDARY + CC.B + "Pearl Cooldown")
    val ARENA: ItemBuilder = ItemBuilder(Material.WATER_LILY)
        .name(CC.SECONDARY + CC.B + "Arena Selection")
    val OLD_COMBAT: ItemBuilder = ItemBuilder(Material.STONE_SWORD)
        .name(CC.SECONDARY + CC.B + "Old Combat Mechanics")
    val REGENERATION: ItemBuilder = ItemBuilder(Material.GOLDEN_APPLE)
        .name(CC.SECONDARY + CC.B + "Regeneration")
    val BOXING: ItemBuilder = ItemBuilder(Material.IRON_CHESTPLATE)
        .name(CC.SECONDARY + CC.B + "Boxing")
    val OTHER_PARTY: ItemBuilder = ItemBuilder(Material.SKULL_ITEM).lore(CC.ACCENT + "Click to duel.")
    val CHOOSE_EXISTING_KNOCKBACK: ItemBuilder = ItemBuilder(Material.PAPER)
        .name(CC.SECONDARY + CC.B + "Choose Existing Knockback")
    val HEALTH_POTIONS_LEFT: ItemBuilder = ItemBuilder(ItemStack(Material.POTION, 1, 16421.toShort()))
        .name(CC.SECONDARY + CC.B + "Health Potions Left")
    val SOUP_LEFT: ItemBuilder = ItemBuilder(Material.MUSHROOM_SOUP).name(CC.SECONDARY + CC.B + "Soup Left")
    val BOT_QUEUE_ENABLED_TEAM: ItemBuilder = ItemBuilder(
        ItemStack(351, 1, DyeColor.LIGHT_BLUE.dyeData.toShort())
    )
        .name(CC.SECONDARY + CC.B + "Team Bot Queue")
    val HEALTH: ItemBuilder = ItemBuilder(ItemStack(Material.POTION, 8193.toShort().toInt()))
    val HITS: ItemBuilder = ItemBuilder(Material.BLAZE_ROD)
    val CLICKS: ItemBuilder = ItemBuilder(Material.GHAST_TEAR)
    val POTION_EFFECTS: ItemBuilder = ItemBuilder(Material.BLAZE_POWDER)
        .name(CC.SECONDARY + CC.B + "Potion Effects")
    val FRICTION: ItemBuilder = ItemBuilder(Material.SOUL_SAND).lore("Click to change value.")
    val HORIZONTAL: ItemBuilder = ItemBuilder(Material.DIODE).lore("Click to change value.")
    val EXTRA_HORIZONTAL: ItemBuilder = ItemBuilder(Material.REDSTONE_COMPARATOR).lore("Click to change value.")
    val VERTICAL: ItemBuilder = ItemBuilder(Material.ARROW).lore("Click to change value.")
    val EXTRA_VERTICAL: ItemBuilder = ItemBuilder(Material.BLAZE_ROD).lore("Click to change value.")
    val VERTICAL_LIMIT: ItemBuilder = ItemBuilder(Material.BEDROCK).lore("Click to change value.")
    val AIM_SPEED: ItemBuilder = ItemBuilder(Material.ICE).lore("Click to change value.")
    val AIM_ACCURACY: ItemBuilder = ItemBuilder(Material.ARROW).lore("Click to change value.")
    val AIM_ERRATICNESS: ItemBuilder = ItemBuilder(Material.LEVER).lore("Click to change value.")
    val AIM_REACTION_TIME: ItemBuilder = ItemBuilder(Material.EMERALD).lore("Click to change value.")
    val BOW_AIMING_RADIUS: ItemBuilder = ItemBuilder(Material.MAP).lore("Click to change value.")
    val REACH: ItemBuilder = ItemBuilder(Material.DIAMOND_SWORD).lore("Click to change value.")
    val SPRINT_RESET_ACCURACY: ItemBuilder = ItemBuilder(Material.DIAMOND_BOOTS).lore("Click to change value.")
    val HIT_SELECT_ACCURACY: ItemBuilder = ItemBuilder(Material.IRON_SWORD).lore("Click to change value.")
    val DISTANCING_MINIMUM: ItemBuilder = ItemBuilder(Material.GOLD_RECORD).lore("Click to change value.")
    val DISTANCING_MAXIMUM: ItemBuilder = ItemBuilder(Material.GREEN_RECORD).lore("Click to change value.")
    val CPS: ItemBuilder = ItemBuilder(Material.SUGAR).lore("Click to change value.")
    val PING: ItemBuilder = ItemBuilder(Material.REDSTONE).lore("Click to change value.")
    val PING_DEVIATION: ItemBuilder = ItemBuilder(Material.GLOWSTONE_DUST).lore("Click to change value.")
    val ARENA_DISABLED: ItemBuilder = ItemBuilder(ItemStack(351, 1, 1.toShort()))
        .lore(CC.RED + "Click to enable arena.")
    val SUBTRACT_1: ItemBuilder = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.RED.data.toShort()))
        .lore("Click to change value.")
    val ADD_1: ItemBuilder = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.GREEN.data.toShort()))
        .lore("Click to change value.")
    val GLOBAL_ELO: ItemBuilder = ItemBuilder(Material.ENDER_PORTAL_FRAME)
    val TOGGLE_PLAYER_VISIBILITY: ItemBuilder = ItemBuilder(Material.GOLDEN_CARROT)
        .name(CC.SECONDARY + CC.B + "Toggle Player Visibility")
    val TOGGLE_DUEL_REQUESTS: ItemBuilder = ItemBuilder(Material.WOOD_SWORD)
        .name(CC.SECONDARY + CC.B + "Toggle Duel Requests")
    val TOGGLE_PARTY_REQUESTS: ItemBuilder = ItemBuilder(Material.NETHER_STAR)
        .name(CC.SECONDARY + CC.B + "Toggle Party Requests")
    val TOGGLE_SCOREBOARD: ItemBuilder = ItemBuilder(Material.ITEM_FRAME)
        .name(CC.SECONDARY + CC.B + "Toggle Scoreboard")
    val TOGGLE_PRIVATE_MESSAGES: ItemBuilder = ItemBuilder(Material.BOOK_AND_QUILL)
        .name(CC.SECONDARY + CC.B + "Toggle Private Messages")
    val TOGGLE_PRIVATE_MESSAGES_SOUNDS: ItemBuilder = ItemBuilder(Material.NOTE_BLOCK)
        .name(CC.SECONDARY + CC.B + "Toggle Private Message Sounds")
    val TOGGLE_FRIENDS_SOUNDS: ItemBuilder = ItemBuilder(Material.JUKEBOX)
        .name(CC.SECONDARY + CC.B + "Toggle Friend Sounds")
    val TOGGLE_FRIEND_REQUESTS: ItemBuilder = ItemBuilder(Material.CAKE)
        .name(CC.SECONDARY + CC.B + "Toggle Friend Requests")
    val TOGGLE_GLOBAL_CHAT: ItemBuilder = ItemBuilder(Material.FEATHER)
        .name(CC.SECONDARY + CC.B + "Toggle Global Chat")
    val NEXT_PAGE: ItemBuilder = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.LIME.data.toShort()))
        .name(CC.SECONDARY + CC.B + "Next Page")
    val PREVIOUS_PAGE: ItemBuilder = ItemBuilder(ItemStack(Material.WOOL, 1, DyeColor.RED.data.toShort()))
        .name(CC.SECONDARY + CC.B + "Previous Page")
    val CHANGE_TIME: ItemBuilder = ItemBuilder(Material.WATCH).name(CC.SECONDARY + CC.B + "Change Time")
}
