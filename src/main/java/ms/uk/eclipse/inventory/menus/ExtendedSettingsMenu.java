package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;

import ms.uk.eclipse.core.inventory.menus.SettingsMenu;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;

public class ExtendedSettingsMenu extends SettingsMenu {

	@Override
	public void init() {
		super.init();
		setSlot(3,
				new ItemBuilder(Material.GOLDEN_CARROT)
						.name(new ChatMessage("Toggle Player Visibility", CC.WHITE, true).toString()).build(),
				new CommandTask("toggleplayervisibility"));
		setSlot(4,
				new ItemBuilder(Material.WOOD_SWORD)
						.name(new ChatMessage("Toggle Duel Requests", CC.WHITE, true).toString()).build(),
				new CommandTask("toggleduelrequests"));
	}
}
