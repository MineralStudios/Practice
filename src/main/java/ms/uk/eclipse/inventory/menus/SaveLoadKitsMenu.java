package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.PracticeMenu;

public class SaveLoadKitsMenu extends PracticeMenu {
    final static String TITLE = CC.BLUE + "Save/Load Kits";

    public SaveLoadKitsMenu() {
        super(TITLE);
        setClickCancelled(true);
    }

    @Override
    public boolean update() {
        ItemStack item = new ItemBuilder(new ItemStack(160, 1, (short) 13))
                .name("Save Kit").build();
        Runnable r = viewer.getPlayerStatus() == PlayerStatus.KIT_CREATOR ? viewer::saveCreatedKit : viewer::saveKit;
        setSlot(4, item, r);
        return true;
    }
}
