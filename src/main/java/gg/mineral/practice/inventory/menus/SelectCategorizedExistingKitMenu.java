package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

@ClickCancelled(true)
public class SelectCategorizedExistingKitMenu extends SelectExistingKitMenu {
	private final Catagory catagory;

	public SelectCategorizedExistingKitMenu(Catagory catagory, PracticeMenu menu, boolean simple) {
		super(menu, simple);
		this.catagory = catagory;
	}

	@Override
	public String getTitle() {
		return CC.BLUE + catagory.getName();
	}

	@Override
	public void update() {
		for (Gametype g : catagory.getGametypes()) {
			ItemStack item = new ItemBuilder(g.getDisplayItem().clone())
					.name(CC.SECONDARY + CC.B + g.getDisplayName()).lore(CC.ACCENT + "Click to select.").build();
			add(item, interaction -> {
				if (viewer.isInKitCreator()) {
					viewer.giveKit(g.getKit());
					return;
				}

				if (simple)
					viewer.getDuelSettings().setGametype(g);
				else
					viewer.getDuelSettings().setKit(g.getKit());

				viewer.openMenu(menu);
			});
		}
	}
}
