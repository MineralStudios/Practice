package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import land.strafe.server.combat.KnockbackProfile;
import land.strafe.server.combat.KnockbackProfileList;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.inventory.Menu;

public class SelectKnockbackMenu extends Menu {
    MechanicsMenu menu;

    public SelectKnockbackMenu(MechanicsMenu menu) {
        super(new StrikingMessage("Select Knockback", CC.PRIMARY, true));
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        for (KnockbackProfile k : KnockbackProfileList.getKnockbackProfiles()) {
            try {
                ItemStack item = new ItemBuilder(Material.GOLD_SWORD)
                        .name(new ChatMessage(k.getName(), CC.WHITE, true).toString()).build();

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
