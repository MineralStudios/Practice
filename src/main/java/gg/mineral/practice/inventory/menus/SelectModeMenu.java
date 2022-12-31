package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
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

		setSlot(2, ItemStacks.SIMPLE_MODE, p -> {
			p.openMenu(new SelectExistingKitMenu(new SelectArenaMenu(action), true));
			return true;
		});

		setSlot(6, ItemStacks.ADVANCED_MODE, p -> {
			p.openMenu(new MechanicsMenu(action));
			return true;
		});

		return true;
	}
}
