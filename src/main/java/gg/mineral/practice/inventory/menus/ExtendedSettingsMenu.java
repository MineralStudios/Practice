package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;

import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class ExtendedSettingsMenu extends PracticeMenu {

	public ExtendedSettingsMenu() {
		super(CC.BLUE + "Settings");
	}

	@Override
	public boolean update() {
		setSlot(4,
				new ItemBuilder(Material.GOLDEN_CARROT)
						.name("Toggle Player Visibility").build(),
				p -> p.bukkit().performCommand("toggleplayervisibility"));
		setSlot(5,
				new ItemBuilder(Material.WOOD_SWORD)
						.name("Toggle Duel Requests").build(),
				p -> p.bukkit().performCommand("toggleduelrequests"));
		return true;
	}
}
