package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.core.utils.item.ItemBuilder;
import gg.mineral.core.utils.message.CC;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.tasks.MenuTask;

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

		if (action == SubmitAction.TOURNAMENT) {
			ItemStack item3 = new ItemBuilder(Material.RECORD_4)
					.name("Tournament Mode").lore()
					.build();
			setSlot(4, item3, new MenuTask(new SelectTournamentMenu()));
		}

		setSlot(2, item, new MenuTask(new SelectExistingKitMenu(new SelectArenaMenu(action), true)));
		setSlot(6, item2, new MenuTask(new MechanicsMenu(action)));
		return true;
	}
}
