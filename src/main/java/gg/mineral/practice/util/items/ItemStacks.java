package gg.mineral.practice.util.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.messages.CC;

public class ItemStacks {
        // Item Stacks
        public static final ItemStack STOP_FOLLOWING = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Stop Following").lore(CC.ACCENT + "Right click to stop following.")
                        .build(),
                        STOP_SPECTATING = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                                        .name(CC.SECONDARY + CC.B + "Stop Spectating")
                                        .lore(CC.ACCENT + "Right click to stop spectating.").build(),
                        WAIT_TO_LEAVE = new ItemBuilder(new ItemStack(351, 1, (short) 14))
                                        .name(CC.SECONDARY + CC.B + "Please Wait")
                                        .lore(CC.ACCENT + "You must wait before leaving.").build(),
                        LEAVE_TOURNAMENT = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                                        .name(CC.SECONDARY + CC.B + "Leave Tournament")
                                        .lore(CC.ACCENT + "Right click to leave.").build(),
                        LEAVE_EVENT = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                                        .name(CC.SECONDARY + CC.B + "Leave Event")
                                        .lore(CC.ACCENT + "Right click to leave.").build(),
                        LEAVE_PARTY = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                                        .name(CC.SECONDARY + CC.B + "Leave Party")
                                        .lore(CC.ACCENT + "Right click to leave.").build(),
                        LEAVE_QUEUE = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                                        .name(CC.SECONDARY + CC.B + "Leave Queue")
                                        .lore(CC.ACCENT + "Right click to leave.").build(),
                        QUEUE_MANAGER = new ItemBuilder(Material.ITEM_FRAME).name(CC.SECONDARY + CC.B + "Queue Manager")
                                        .lore(CC.ACCENT + "Right click to manage queued game types.")
                                        .build(),
                        QUEUE = new ItemBuilder(Material.COMPASS).name(CC.SECONDARY + CC.B + "Queue")
                                        .lore(CC.ACCENT + "Right click to queue.").build(),
                        QUEUE_AGAIN = new ItemBuilder(Material.PAPER).name(CC.SECONDARY + CC.B + "Queue Again")
                                        .lore(CC.ACCENT + "Right click to queue again.").build(),
                        LIST_PLAYERS = new ItemBuilder(Material.PAPER).name(CC.SECONDARY + CC.B + "List Players")
                                        .lore(CC.ACCENT + "Right click to list players.")
                                        .build(),
                        DUEL = new ItemBuilder(Material.WOOD_AXE).name(CC.SECONDARY + CC.B + "Duel")
                                        .lore(CC.ACCENT + "Right click to duel.").build(),
                        SETTINGS = new ItemBuilder(Material.MAP).name(CC.SECONDARY + CC.B + "Settings")
                                        .build(),
                        PARTY_SPLIT = new ItemBuilder(Material.GOLD_AXE).name(CC.SECONDARY + CC.B + "Party Split")
                                        .lore(CC.ACCENT + "Right click to start party split.")
                                        .build(),
                        OPEN_PARTY = new ItemBuilder(Material.SKULL_ITEM).name(CC.SECONDARY + CC.B + "Open Party")
                                        .lore(CC.ACCENT + "Right click to open party.")
                                        .build(),
                        NO_HEALTH = new ItemBuilder(Material.SKULL_ITEM).name("Health: 0").build(),
                        VIEW_OPPONENT_INVENTORY = new ItemBuilder(Material.LEVER).name("View Opponent Inventory")
                                        .build(),
                        SUBMIT = new ItemBuilder(Material.STICK).name("Submit").build(),
                        RESET_SETTINGS = new ItemBuilder(Material.PAPER).name("Reset Settings").build(),
                        SAVE_KIT = new ItemBuilder(Material.ENCHANTED_BOOK)
                                        .name("Save Kit").build(),
                        DELETE_KIT = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                                        .name("Delete Kit").build(),
                        CHOOSE_EXISTING_KIT = new ItemBuilder(Material.LEATHER_CHESTPLATE).name("Choose Existing Kit")
                                        .build(),
                        CHOOSE_EXISTING_KNOCKBACK = new ItemBuilder(Material.GOLD_SWORD)
                                        .name(CC.SECONDARY + CC.B + "Choose Existing Knockback")
                                        .lore(CC.WHITE + "Choose a " + CC.SECONDARY + "preconfigured knockback"
                                                        + CC.WHITE + ".",
                                                        " ",
                                                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select knockback.")
                                        .build(),
                        CREATE_CUSTOM_KNOCKBACK = new ItemBuilder(Material.STONE_SWORD)
                                        .name(CC.SECONDARY + CC.B + "Create Custom Knockback")
                                        .lore(CC.WHITE + "Create a " + CC.SECONDARY + "custom knockback"
                                                        + CC.WHITE + ".",
                                                        " ",
                                                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to create knockback.")
                                        .build(),
                        CHOOSE_CUSTOM_KIT = new ItemBuilder(Material.GOLD_CHESTPLATE).name("Create Custom Kit").build(),
                        SIMPLE_MODE = new ItemBuilder(Material.GREEN_RECORD).name(CC.SECONDARY + CC.B + "Simple Mode")
                                        .lore(CC.WHITE + "A more " + CC.SECONDARY + "simple and familiar", CC.WHITE
                                                        + "duel interface.",
                                                        " ",
                                                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select.")
                                        .build(),
                        ADVANCED_MODE = new ItemBuilder(Material.GOLD_RECORD)
                                        .name(CC.SECONDARY + CC.B + "Advanced Mode")
                                        .lore(CC.WHITE + "A more " + CC.SECONDARY + "advanced" + CC.WHITE
                                                        + " duel interface", CC.WHITE + "with many options.",
                                                        " ",
                                                        CC.BOARD_SEPARATOR, CC.ACCENT + "Click to select.")
                                        .build(),
                        AIR = new ItemStack(Material.AIR), BLACK_STAINED_GLASS = new ItemBuilder(
                                        new ItemStack(Material.STAINED_GLASS_PANE, 1,
                                                        DyeColor.BLACK.getData()))
                                        .name(null).build(),
                        DEFAULT_ARENA_DISPLAY_ITEM = new ItemStack(Material.WOOL),
                        DEFAULT_CATAGORY_DISPLAY_ITEM = new ItemStack(Material.DIAMOND_SWORD),
                        DEFAULT_GAMETYPE_DISPLAY_ITEM = new ItemStack(Material.DIAMOND_SWORD),
                        EMPTY_BOWL = new ItemStack(Material.BOWL),
                        DEFAULT_KIT_EDITOR_DISPLAY_ITEM = new ItemStack(Material.BOOK),
                        DEFAULT_OPTIONS_DISPLAY_ITEM = new ItemStack(Material.COMPASS),
                        DEFAULT_SPECTATE_DISPLAY_ITEM = new ItemStack(Material.HOPPER),
                        DEFAULT_LEADERBOARD_DISPLAY_ITEM = new ItemStack(Material.SKULL_ITEM, 1,
                                        (short) SkullType.PLAYER.ordinal()),
                        DEFAULT_PARTY_DISPLAY_ITEM = new ItemStack(Material.NETHER_STAR),
                        DEFAULT_QUEUETYPE_DISPLAY_ITEM = new ItemStack(Material.DIAMOND_SWORD),
                        WOOD_AXE = new ItemStack(Material.WOOD_AXE),
                        APPLY = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData())).name(" ")
                                        .lore(CC.GOLD + CC.B + "Click To Apply Changes", " ").build(),
                        DESELECT_ALL = new ItemBuilder(Material.REDSTONE_BLOCK).name(" ")
                                        .lore(CC.RED + CC.B + "Deselect All", " ").build(),
                        SELECT_ALL = new ItemBuilder(Material.LAPIS_BLOCK).name(" ")
                                        .lore(CC.GREEN + CC.B + "Select All", " ").build(),
                        CANCEL = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData())).name(" ")
                                        .lore(CC.RED + CC.B + "Click To Cancel Changes", " ").build(),
                        RANDOM_QUEUE = new ItemBuilder(Material.PAPER)
                                        .name(CC.SECONDARY + CC.B + "Random Queue")
                                        .lore(CC.WHITE + "Adds you to a " + CC.BLUE + "random" + CC.WHITE + " queue.",
                                                        " ", CC.BOARD_SEPARATOR,
                                                        CC.ACCENT + "Click to be added to a random queue.")
                                        .build(),
                        RANDOM_DIFFICULTY = new ItemBuilder(Material.PAPER)
                                        .name(CC.SECONDARY + CC.B + "Random Difficulty")
                                        .lore(CC.ACCENT + "Click to randomize bot parameters.").build(),
                        EASY = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData())).name("Easy")
                                        .build(),
                        MEDIUM = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.YELLOW.getData()))
                                        .name("Medium")
                                        .build(),
                        HARD = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData())).name("Hard")
                                        .build(),
                        EXPERT = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData())).name("Expert")
                                        .build(),
                        HACKER = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getData()))
                                        .name("Hacker")
                                        .build(),
                        BOT_QUEUE_DISABLED = new ItemBuilder(new ItemStack(351, 1, DyeColor.GRAY.getDyeData()))
                                        .name(CC.SECONDARY + CC.B + "Bot Queue")
                                        .lore(CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "bot" + CC.WHITE
                                                        + " match.", " ",
                                                        CC.WHITE + "Currently:",
                                                        CC.RED + "Disabled",
                                                        " ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle bots.")
                                        .build(),
                        BOT_QUEUE_ENABLED = new ItemBuilder(new ItemStack(351, 1, DyeColor.LIME.getDyeData()))
                                        .name(CC.SECONDARY + CC.B + "Bot Queue")
                                        .lore(CC.WHITE + "Allows you to queue in a " + CC.SECONDARY + "bot" + CC.WHITE
                                                        + " match.", " ",
                                                        CC.WHITE + "Currently:",
                                                        CC.GREEN + "Enabled",
                                                        " ", CC.BOARD_SEPARATOR, CC.ACCENT + "Click to toggle bots.")
                                        .build(),
                        BACK = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                                        .name(CC.SECONDARY + CC.B + "Back").lore(CC.ACCENT + "Click to go back.")
                                        .build();

        // Item Builders
        public static final ItemBuilder LOAD_KIT = new ItemBuilder(Material.ENCHANTED_BOOK),
                        TEAMFIGHT = new ItemBuilder(Material.ITEM_FRAME)
                                        .name(CC.SECONDARY + CC.B + "2v2"),
                        BOT_SETTINGS = new ItemBuilder(Material.GOLD_HELMET)
                                        .name(CC.SECONDARY + CC.B + "Bot Settings"),
                        CLICK_TO_APPLY_CHANGES = new ItemBuilder(Material.STONE_SWORD)
                                        .lore(CC.ACCENT + "Click To Apply Changes"),
                        INVENTORY_STATS = new ItemBuilder(Material.ENDER_CHEST),
                        SELECT_KIT = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                                        .name("Select Kit"),
                        CHANGE_KNOCKBACK = new ItemBuilder(Material.STICK)
                                        .name("Change Knockback"),
                        HIT_DELAY = new ItemBuilder(Material.WATCH)
                                        .name("Hit Delay"),
                        TOGGLE_HUNGER = new ItemBuilder(Material.COOKED_BEEF)
                                        .name("Toggle Hunger"),
                        DEADLY_WATER = new ItemBuilder(Material.BLAZE_ROD)
                                        .name("Deadly Water"),
                        TOGGLE_BUILD = new ItemBuilder(Material.BRICK)
                                        .name("Toggle Build"),
                        TOGGLE_DAMAGE = new ItemBuilder(Material.DIAMOND_AXE)
                                        .name("Toggle Damage"),
                        TOGGLE_GRIEFING = new ItemBuilder(Material.TNT)
                                        .name("Toggle Griefing"),
                        PEARL_COOLDOWN = new ItemBuilder(Material.ENDER_PEARL)
                                        .name("Pearl Cooldown"),
                        ARENA = new ItemBuilder(Material.WATER_LILY)
                                        .name(CC.SECONDARY + CC.B + "Arena Selection"),
                        REGENERATION = new ItemBuilder(Material.GOLDEN_APPLE)
                                        .name("Regeneration"),
                        BOXING = new ItemBuilder(Material.IRON_CHESTPLATE)
                                        .name("Boxing"),
                        OTHER_PARTY = new ItemBuilder(Material.SKULL_ITEM),
                        KNOCKBACK = new ItemBuilder(Material.GOLD_SWORD),
                        HEALTH_POTIONS_LEFT = new ItemBuilder(new ItemStack(Material.POTION, 1, (short) 16421))
                                        .name("Health Potions Left"),
                        SOUP_LEFT = new ItemBuilder(Material.MUSHROOM_SOUP).name("Soup Left"),
                        BOT_QUEUE_ENABLED_2V2 = new ItemBuilder(new ItemStack(351, 1, DyeColor.LIGHT_BLUE.getDyeData()))
                                        .name(CC.SECONDARY + CC.B + "2v2 Bot Queue"),
                        HEALTH = new ItemBuilder(new ItemStack(Material.POTION, (short) 8193)),
                        HITS = new ItemBuilder(Material.BLAZE_ROD), CLICKS = new ItemBuilder(Material.GHAST_TEAR),
                        POTION_EFFECTS = new ItemBuilder(Material.BLAZE_POWDER).name("Potion Effects"),
                        FRICTION = new ItemBuilder(Material.SOUL_SAND).lore("Click to change value."),
                        HORIZONTAL = new ItemBuilder(Material.DIODE).lore("Click to change value."),
                        EXTRA_HORIZONTAL = new ItemBuilder(Material.REDSTONE_COMPARATOR).lore("Click to change value."),
                        VERTICAL = new ItemBuilder(Material.ARROW).lore("Click to change value."),
                        EXTRA_VERTICAL = new ItemBuilder(Material.BLAZE_ROD).lore("Click to change value."),
                        VERTICAL_LIMIT = new ItemBuilder(Material.BEDROCK).lore("Click to change value."),
                        AIM_SPEED = new ItemBuilder(Material.ICE).lore("Click to change value."),
                        AIM_ACCURACY = new ItemBuilder(Material.ARROW).lore("Click to change value."),
                        AIM_REACTION_TIME = new ItemBuilder(Material.EMERALD).lore("Click to change value."),
                        BOW_AIMING_RADIUS = new ItemBuilder(Material.MAP)
                                        .lore("Click to change value."),
                        REACH = new ItemBuilder(Material.DIAMOND_SWORD).lore("Click to change value."),
                        SPRINT_RESET_ACCURACY = new ItemBuilder(Material.DIAMOND_BOOTS).lore("Click to change value."),
                        HIT_SELECT_ACCURACY = new ItemBuilder(Material.IRON_SWORD).lore("Click to change value."),
                        DISTANCING_MINIMUM = new ItemBuilder(Material.GOLD_RECORD)
                                        .lore("Click to change value."),
                        DISTANCING_MAXIMUM = new ItemBuilder(Material.GREEN_RECORD).lore("Click to change value."),
                        CPS = new ItemBuilder(Material.SUGAR).lore("Click to change value."),
                        PING = new ItemBuilder(Material.REDSTONE).lore("Click to change value."),
                        ARENA_DISABLED = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                                        .lore(CC.RED + "Click to enable arena."),
                        SUBTRACT_1 = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                                        .lore("Click to change value."),
                        ADD_1 = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()))
                                        .lore("Click to change value."),
                        GLOBAL_ELO = new ItemBuilder(Material.ENDER_PORTAL_FRAME),
                        TOGGLE_PLAYER_VISIBILITY = new ItemBuilder(Material.GOLDEN_CARROT)
                                        .name(CC.SECONDARY + CC.B + "Toggle Player Visibility"),
                        TOGGLE_DUEL_REQUESTS = new ItemBuilder(Material.WOOD_SWORD)
                                        .name(CC.SECONDARY + CC.B + "Toggle Duel Requests"),
                        TOGGLE_PARTY_REQUESTS = new ItemBuilder(Material.NETHER_STAR)
                                        .name(CC.SECONDARY + CC.B + "Toggle Party Requests"),
                        TOGGLE_SCOREBOARD = new ItemBuilder(Material.ITEM_FRAME)
                                        .name(CC.SECONDARY + CC.B + "Toggle Scoreboard"),
                        TOGGLE_PRIVATE_MESSAGES = new ItemBuilder(Material.BOOK_AND_QUILL)
                                        .name(CC.SECONDARY + CC.B + "Toggle Private Messages"),
                        TOGGLE_PRIVATE_MESSAGES_SOUNDS = new ItemBuilder(Material.NOTE_BLOCK)
                                        .name(CC.SECONDARY + CC.B + "Toggle Private Message Sounds"),
                        TOGGLE_FRIENDS_SOUNDS = new ItemBuilder(Material.JUKEBOX)
                                        .name(CC.SECONDARY + CC.B + "Toggle Friend Sounds"),
                        TOGGLE_FRIEND_REQUESTS = new ItemBuilder(Material.CAKE)
                                        .name(CC.SECONDARY + CC.B + "Toggle Friend Requests"),
                        TOGGLE_GLOBAL_CHAT = new ItemBuilder(Material.FEATHER)
                                        .name(CC.SECONDARY + CC.B + "Toggle Global Chat"),
                        NEXT_PAGE = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData()))
                                        .name(CC.SECONDARY + CC.B + "Next Page"),
                        PREVIOUS_PAGE = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                                        .name(CC.SECONDARY + CC.B + "Previous Page"),
                        CHANGE_TIME = new ItemBuilder(Material.WATCH)
                                        .name(CC.SECONDARY + CC.B + "Change Time");

}
