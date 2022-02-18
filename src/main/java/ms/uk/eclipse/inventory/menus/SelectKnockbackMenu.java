package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import land.strafe.server.combat.KnockbackProfile;
import land.strafe.server.combat.KnockbackProfileList;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.inventory.PracticeMenu;

public class SelectKnockbackMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Select Knockback";

    public SelectKnockbackMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        for (KnockbackProfile k : KnockbackProfileList.getKnockbackProfiles()) {
            try {
                ItemStack item = new ItemBuilder(Material.GOLD_SWORD)
                        .name(k.getName()).build();

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        viewer.getMatchData().setKnockback(k);
                        viewer.openMenu(menu);
                    }
                };

                add(item, runnable);
            } catch (NullPointerException e) {
                continue;
            }
        }

        return true;
    }
}
