package gg.mineral.practice.inventory.menus;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;

public class HitDelayMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Modify Value";

    public HitDelayMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        ItemStack item = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()))
                .name("SUBTRACT 1").build();
        ItemStack item2 = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.GREEN.getData()))
                .name("ADD 1").build();
        ItemStack item3 = new ItemBuilder(Material.STONE_SWORD)
                .name("Hit Delay: " + viewer.getMatchData().getNoDamageTicks())
                .lore(CC.ACCENT + "Click To Apply Changes").build();
        HitDelayMenu hitMenu = this;

        Runnable subtractTask = () -> {
            if (viewer.getMatchData().getNoDamageTicks() >= 1) {
                viewer.getMatchData()
                        .setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() - 1);
            }

            viewer.openMenu(hitMenu);
        };

        setSlot(2, item, subtractTask);

        Runnable addTask = () -> {
            viewer.getMatchData().setNoDamageTicks(viewer.getMatchData().getNoDamageTicks() + 1);
            viewer.openMenu(hitMenu);
        };

        setSlot(6, item2, addTask);
        setSlot(4, item3, p -> {
            p.openMenu(menu);
            return true;
        });

        return true;
    }
}
