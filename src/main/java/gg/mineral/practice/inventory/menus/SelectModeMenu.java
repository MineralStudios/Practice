package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;

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

		setSlot(2, ItemStacks.SIMPLE_MODE, interaction -> {
			Profile p = interaction.getProfile();
			p.resetMatchData();
			p.openMenu(new SelectExistingKitMenu(new SelectArenaMenu(action), true));
		});

		setSlot(6, ItemStacks.ADVANCED_MODE, interaction -> {
			Profile p = interaction.getProfile();
			p.openMenu(new MechanicsMenu(action));
		});

		return true;
	}
}
