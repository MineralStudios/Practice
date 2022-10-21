package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.practice.inventory.SubmitAction;

public class SelectModeMenu implements InventoryBuilder {
	SubmitAction action;
	final static String TITLE = CC.BLUE + "Select Mode";

	public SelectModeMenu(SubmitAction action) {
		super(TITLE);
		setItemDragging(true);
		this.action = action;
	}

	@Override
	public MineralInventory build(Profile profile) {
		viewer.setPreviousSubmitAction(action);
		ItemStack item = new ItemBuilder(Material.GREEN_RECORD)
				.name("Simple Mode").lore().build();
		ItemStack item2 = new ItemBuilder(Material.GOLD_RECORD)
				.name("Advanced Mode").lore().build();

		if (action == SubmitAction.TOURNAMENT) {
			ItemStack item3 = new ItemBuilder(Material.RECORD_4)
					.name("Tournament Mode").lore()
					.build();
			set(4, item3, new MenuTask(new SelectTournamentMenu()));
		}

		set(2, item, new MenuTask(new SelectExistingKitMenu(new SelectArenaMenu(action), true)));
		set(6, item2, new MenuTask(new MechanicsMenu(action)));
		return true;
	}
}
