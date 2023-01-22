package gg.mineral.practice.util.items;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.messages.CC;

public class ItemStacks {
        // Item Stacks
        public static final ItemStack STOP_FOLLOWING = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Stop Following").build();
        public static final ItemStack STOP_SPECTATING = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Stop Spectating").build();
        public static final ItemStack WAIT_TO_LEAVE = new ItemBuilder(new ItemStack(351, 1, (short) 14))
                        .name(CC.SECONDARY + CC.B + "Please Wait").build();
        public static final ItemStack LEAVE_TOURNAMENT = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Leave Tournament").build();
        public static final ItemStack LEAVE_EVENT = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Leave Event").build();
        public static final ItemStack LEAVE_PARTY = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Leave Party").build();
        public static final ItemStack LEAVE_QUEUE = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Leave Queue").build();
        public static final ItemStack QUEUE_MANAGER = new ItemBuilder(Material.ITEM_FRAME)
                        .name(CC.SECONDARY + CC.B + "Queue Manager").build();
        public static final ItemStack QUEUE = new ItemBuilder(Material.COMPASS)
                        .name(CC.SECONDARY + CC.B + "Queue").build();
        public static final ItemStack QUEUE_AGAIN = new ItemBuilder(Material.PAPER)
                        .name(CC.SECONDARY + CC.B + "Queue Again").build();
        public static final ItemStack LIST_PLAYERS = new ItemBuilder(Material.PAPER)
                        .name(CC.SECONDARY + CC.B + "List Players").build();
        public static final ItemStack DUEL = new ItemBuilder(Material.WOOD_AXE)
                        .name(CC.SECONDARY + CC.B + "Duel").build();
        public static final ItemStack PARTY_SPLIT = new ItemBuilder(Material.GOLD_AXE)
                        .name(CC.SECONDARY + CC.B + "Party Split").build();
        public static final ItemStack OPEN_PARTY = new ItemBuilder(Material.SKULL_ITEM)
                        .name(CC.SECONDARY + CC.B + "Open Party").build();
        public static final ItemStack NO_HEALTH = new ItemBuilder(Material.SKULL_ITEM)
                        .name("Health: 0").build();
        public static final ItemStack SUBTRACT_1 = new ItemBuilder(
                        new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                        .name("SUBTRACT 1").build();
        public static final ItemStack ADD_1 = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()))
                        .name("ADD 1").build();
        public static final ItemStack VIEW_OPPONENT_INVENTORY = new ItemBuilder(Material.LEVER)
                        .name("View Opponent Inventory").build();
        public static final ItemStack SUBMIT = new ItemBuilder(Material.STICK)
                        .name("Submit").build();
        public static final ItemStack RESET_SETTINGS = new ItemBuilder(Material.PAPER)
                        .name("Reset Settings").build();
        public static final ItemStack SAVE_KIT = new ItemBuilder(
                        new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()))
                        .name("Save Kit").build();
        public static final ItemStack DELETE_KIT = new ItemBuilder(
                        new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                        .name("Delete Kit").build();
        public static final ItemStack CHOOSE_EXISTING_KIT = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .name("Choose Existing Kit").build();
        public static final ItemStack CHOOSE_CUSTOM_KIT = new ItemBuilder(Material.GOLD_CHESTPLATE)
                        .name("Create Custom Kit").build();
        public static final ItemStack SIMPLE_MODE = new ItemBuilder(Material.GREEN_RECORD)
                        .name("Simple Mode").lore().build();
        public static final ItemStack ADVANCED_MODE = new ItemBuilder(Material.GOLD_RECORD)
                        .name("Advanced Mode").lore().build();
        public static final ItemStack TOGGLE_PLAYER_VISIBILITY = new ItemBuilder(Material.GOLDEN_CARROT)
                        .name("Toggle Player Visibility").build();
        public static final ItemStack TOGGLE_DUEL_REQUESTS = new ItemBuilder(Material.WOOD_SWORD)
                        .name("Toggle Duel Requests").build();
        public static final ItemStack TOGGLE_PARTY_REQUESTS = new ItemBuilder(Material.NETHER_STAR)
                        .name("Toggle Party Requests").build();
        public static final ItemStack NEXT_PAGE = new ItemBuilder(
                        new ItemStack(Material.WOOL, 1, DyeColor.LIME.getData()))
                        .name("Next Page").build();
        public static final ItemStack PREVIOUS_PAGE = new ItemBuilder(
                        new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                        .name("Previous Page").build();
        public static final ItemStack AIR = new ItemStack(Material.AIR);
        public static final ItemStack BLACK_STAINED_GLASS = new ItemBuilder(
                        new ItemStack(Material.STAINED_GLASS_PANE, 1,
                                        DyeColor.BLACK.getData()))
                        .name(null).build();
        public static final ItemStack DEFAULT_ARENA_DISPLAY_ITEM = new ItemStack(Material.WOOL);
        public static final ItemStack DEFAULT_CATAGORY_DISPLAY_ITEM = new ItemStack(Material.DIAMOND_SWORD);
        public static final ItemStack DEFAULT_GAMETYPE_DISPLAY_ITEM = new ItemStack(Material.DIAMOND_SWORD);
        public static final ItemStack EMPTY_BOWL = new ItemStack(Material.BOWL);
        public static final ItemStack DEFAULT_KIT_EDITOR_DISPLAY_ITEM = new ItemStack(Material.BOOK);
        public static final ItemStack DEFAULT_OPTIONS_DISPLAY_ITEM = new ItemStack(Material.COMPASS);
        public static final ItemStack DEFAULT_PARTY_DISPLAY_ITEM = new ItemStack(Material.NETHER_STAR);
        public static final ItemStack DEFAULT_QUEUETYPE_DISPLAY_ITEM = new ItemStack(Material.DIAMOND_SWORD);
        public static final ItemStack WOOD_AXE = new ItemStack(Material.WOOD_AXE);
        // Item Builders
        public static final ItemBuilder LOAD_KIT = new ItemBuilder(Material.BOOK);
        public static final ItemBuilder CLICK_TO_APPLY_CHANGES = new ItemBuilder(Material.STONE_SWORD)
                        .lore(CC.ACCENT + "Click To Apply Changes");
        public static final ItemBuilder INVENTORY_STATS = new ItemBuilder(Material.ENDER_CHEST);
        public static final ItemBuilder SELECT_KIT = new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                        .name("Select Kit");
        public static final ItemBuilder CHANGE_KNOCKBACK = new ItemBuilder(Material.STICK)
                        .name("Change Knockback");
        public static final ItemBuilder HIT_DELAY = new ItemBuilder(Material.WATCH)
                        .name("Hit Delay");
        public static final ItemBuilder TOGGLE_HUNGER = new ItemBuilder(Material.COOKED_BEEF)
                        .name("Toggle Hunger");
        public static final ItemBuilder DEADLY_WATER = new ItemBuilder(Material.BLAZE_ROD)
                        .name("Deadly Water");
        public static final ItemBuilder TOGGLE_BUILD = new ItemBuilder(Material.BRICK)
                        .name("Toggle Build");
        public static final ItemBuilder TOGGLE_DAMAGE = new ItemBuilder(Material.DIAMOND_AXE)
                        .name("Toggle Damage");
        public static final ItemBuilder TOGGLE_GRIEFING = new ItemBuilder(Material.TNT)
                        .name("Toggle Griefing");
        public static final ItemBuilder PEARL_COOLDOWN = new ItemBuilder(Material.ENDER_PEARL)
                        .name("Pearl Cooldown");
        public static final ItemBuilder ARENA = new ItemBuilder(Material.WATER_LILY)
                        .name("Arena");
        public static final ItemBuilder REGENERATION = new ItemBuilder(Material.GOLDEN_APPLE)
                        .name("Regeneration");
        public static final ItemBuilder OTHER_PARTY = new ItemBuilder(Material.SKULL_ITEM);
        public static final ItemBuilder KNOCKBACK = new ItemBuilder(Material.GOLD_SWORD);
        public static final ItemBuilder HEALTH_POTIONS_LEFT = new ItemBuilder(
                        new ItemStack(Material.POTION, 1, (short) 16421))
                        .name("Health Potions Left");
        public static final ItemBuilder SOUP_LEFT = new ItemBuilder(Material.MUSHROOM_SOUP)
                        .name("Soup Left");
        public static final ItemBuilder HEALTH = new ItemBuilder(
                        new ItemStack(Material.POTION, (short) 8193));
        public static final ItemBuilder HITS = new ItemBuilder(Material.BLAZE_ROD);
        public static final ItemBuilder CLICKS = new ItemBuilder(Material.GHAST_TEAR);
        public static final ItemBuilder POTION_EFFECTS = new ItemBuilder(Material.BLAZE_POWDER)
                        .name("Potion Effects");

}
