package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.tasks.MenuTask;

public class SelectKitMenu extends Menu {
    MechanicsMenu menu;

    public SelectKitMenu(MechanicsMenu menu) {
        super(new StrikingMessage("Select Kit", CC.PRIMARY, true));
        setClickCancelled(true);
        this.menu = menu;
    }

    @Override
    public boolean update() {
        ItemStack item = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .name(new ChatMessage("Choose Existing Kit", CC.PRIMARY, false).toString()).build();
        ItemStack item2 = new ItemBuilder(Material.GOLD_CHESTPLATE)
                .name(new ChatMessage("Create Custom Kit", CC.PRIMARY, false).toString()).build();
        setSlot(2, item, new MenuTask(new SelectExistingKitMenu(menu, false)));
        Runnable runnable = viewer::sendPlayerToKitCreator;
        setSlot(6, item2, runnable);
        return true;
    }
}
