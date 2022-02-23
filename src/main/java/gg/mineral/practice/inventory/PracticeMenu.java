package gg.mineral.practice.inventory;

import gg.mineral.core.inventory.Menu;
import gg.mineral.practice.entity.Profile;

public class PracticeMenu extends Menu {
	protected Profile viewer = null;

	public PracticeMenu(String title) {
		super(title);
	}

	public PracticeMenu(PracticeMenu menu) {
		super(menu);
	}

	public void open(Profile player) {
		viewer = player;

		if (needsUpdate) {
			needsUpdate = update();
		}

		if (needsUpdate || inv == null) {
			inv = toInventory(player.bukkit());
		}

		player.bukkit().openInventory(inv);
		player.setOpenMenu(this);
	}
}
