package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.Menu;

public class SaveLoadKitsMenu extends Menu {
    public SaveLoadKitsMenu() {
        super(new StrikingMessage("Save/Load Kits", CC.PRIMARY, true));
        setClickCancelled(true);
    }

    public void update() {
        ItemStack item = new ItemBuilder(new ItemStack(160, 1, (short) 13))
                .name(new ChatMessage("Save Kit", CC.PRIMARY, false).toString()).build();
        Runnable r = viewer.getPlayerStatus() == PlayerStatus.KIT_CREATOR ? viewer::saveCreatedKit : viewer::saveKit;
        setSlot(4, item, r);
    }
}
