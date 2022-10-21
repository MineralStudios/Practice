package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;

public class SelectKnockbackMenu implements InventoryBuilder {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Select Knockback";

    public SelectKnockbackMenu(MechanicsMenu menu) {
        super(TITLE);
        setItemDragging(true);
        this.menu = menu;
    }

    @Override
    public MineralInventory build(Profile profile) {
        for (KnockbackProfile k : KnockbackProfileList.getKnockbackProfiles()) {
            try {
                ItemStack item = new ItemBuilder(Material.GOLD_SWORD)
                        .name(k.getName()).build();

                Runnable runnable = () -> {
                    viewer.getMatchData().setKnockback(k);
                    viewer.openMenu(menu);
                };

                add(item, runnable);
            } catch (NullPointerException e) {
                continue;
            }
        }

        return true;
    }
}
