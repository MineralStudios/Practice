package gg.mineral.practice.util.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
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
        public static final ItemStack LEAVE_PARTY = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Leave Party").build();
        public static final ItemStack LEAVE_QUEUE = new ItemBuilder(new ItemStack(351, 1, (short) 1))
                        .name(CC.SECONDARY + CC.B + "Leave Queue").build();
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
}
