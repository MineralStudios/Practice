package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.tasks.MenuTask;

public class SelectKitMenu extends PracticeMenu {
    MechanicsMenu menu;
    final static String TITLE = CC.BLUE + "Select Kit";

    public SelectKitMenu(MechanicsMenu menu) {
        super(TITLE);
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        ItemStack item = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .name("Choose Existing Kit").build();
        ItemStack item2 = new ItemBuilder(Material.GOLD_CHESTPLATE)
                .name("Create Custom Kit").build();
        setSlot(2, item, new MenuTask(new SelectExistingKitMenu(menu, false)));
        Runnable runnable = viewer::sendPlayerToKitCreator;
        setSlot(6, item2, runnable);
        return true;
    }
}
