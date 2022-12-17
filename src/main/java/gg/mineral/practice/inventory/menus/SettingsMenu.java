package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;

import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class SettingsMenu extends PracticeMenu {

	public SettingsMenu() {
		super(CC.BLUE + "Settings");
		setClickCancelled(true);
	}

	@Override
	public boolean update() {
		setSlot(0,
				new ItemBuilder(Material.GOLDEN_CARROT)
						.name("Toggle Player Visibility").build(),
				p -> p.getPlayer().performCommand("toggleplayervisibility"));
		setSlot(1,
				new ItemBuilder(Material.WOOD_SWORD)
						.name("Toggle Duel Requests").build(),
				p -> p.getPlayer().performCommand("toggleduelrequests"));
		setSlot(1,
				new ItemBuilder(Material.NETHER_STAR)
						.name("Toggle Party Requests").build(),
				p -> p.getPlayer().performCommand("togglepartyrequests"));
		return true;
	}
}
