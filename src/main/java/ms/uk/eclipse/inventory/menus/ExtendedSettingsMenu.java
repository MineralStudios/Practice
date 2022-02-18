package ms.uk.eclipse.inventory.menus;

import org.bukkit.Material;

import ms.uk.eclipse.core.inventory.menus.SettingsMenu;
import ms.uk.eclipse.core.tasks.CommandTask;
import ms.uk.eclipse.core.utils.item.ItemBuilder;

public class ExtendedSettingsMenu extends SettingsMenu {

	@Override
	public boolean update() {
		boolean returnVal = super.update();
		setSlot(4,
				new ItemBuilder(Material.GOLDEN_CARROT)
						.name("Toggle Player Visibility").build(),
				new CommandTask("toggleplayervisibility"));
		setSlot(5,
				new ItemBuilder(Material.WOOD_SWORD)
						.name("Toggle Duel Requests").build(),
				new CommandTask("toggleduelrequests"));
		return returnVal;
	}
}
