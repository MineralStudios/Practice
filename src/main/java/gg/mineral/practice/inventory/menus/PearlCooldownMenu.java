package gg.mineral.practice.inventory.menus;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.api.inventory.InventoryBuilder;

public class PearlCooldownMenu implements InventoryBuilder {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Modify Value";

    public PearlCooldownMenu(MechanicsMenu menu) {
        super(TITLE);
        setItemDragging(true);
        this.menu = menu;
    }

    @Override
    public MineralInventory build(Profile profile) {
        ItemStack item = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                .name("SUBTRACT 1").build();
        ItemStack item2 = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()))
                .name("ADD 1").build();
        ItemStack item3 = new ItemBuilder(Material.STONE_SWORD)
                .name("Pearl Cooldown: " + viewer.getMatchData().getPearlCooldown())
                .lore(CC.ACCENT + "Click To Apply Changes").build();
        PearlCooldownMenu pMenu = this;
        Runnable subtractTask = () -> {
            if (viewer.getMatchData().getPearlCooldown() >= 1) {
                viewer.getMatchData().setPearlCooldown(viewer.getMatchData().getPearlCooldown() - 1);
            }
            viewer.openMenu(pMenu);
        };
        set(2, item, subtractTask);
        Runnable addTask = () -> {
            if (viewer.getMatchData().getPearlCooldown() >= 1) {
                viewer.getMatchData().setPearlCooldown(viewer.getMatchData().getPearlCooldown() + 1);
            }
            viewer.openMenu(pMenu);
        };
        set(6, item2, addTask);
        set(4, item3, new MenuTask(menu));
        return true;
    }
}
