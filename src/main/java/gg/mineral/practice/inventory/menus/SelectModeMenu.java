package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;

public class SelectModeMenu extends PracticeMenu {
	SubmitAction action;
	final static String TITLE = CC.BLUE + "Select Mode";

	public SelectModeMenu(SubmitAction action) {
		super(TITLE);
		setClickCancelled(true);
		this.action = action;
	}

	@Override
	public boolean update() {
		viewer.setPreviousSubmitAction(action);
		ItemStack item = new ItemBuilder(Material.GREEN_RECORD)
				.name("Simple Mode").lore().build();
		ItemStack item2 = new ItemBuilder(Material.GOLD_RECORD)
				.name("Advanced Mode").lore().build();

		setSlot(2, item, p -> {
			p.openMenu(new SelectExistingKitMenu(new SelectArenaMenu(action), true));
			return true;
		});
		setSlot(6, item2, p -> {
			p.openMenu(new MechanicsMenu(action));
			return true;
		});
		return true;
	}
}
