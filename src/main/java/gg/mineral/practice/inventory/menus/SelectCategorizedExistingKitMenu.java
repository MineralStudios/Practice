package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class SelectCategorizedExistingKitMenu extends SelectExistingKitMenu {
	Catagory c;

	public SelectCategorizedExistingKitMenu(Catagory c, PracticeMenu menu, boolean simple) {
		super(menu, simple);
		setTitle(CC.BLUE + c.getName());
		this.c = c;
	}

	@Override
	public boolean update() {
		for (Gametype g : c.getGametypes()) {
			ItemStack item = new ItemBuilder(g.getDisplayItem().clone())
					.name(CC.SECONDARY + CC.B + g.getDisplayName()).lore(CC.ACCENT + "Click to select.").build();
			add(item, () -> {
				if (viewer.isInKitCreator()) {
					viewer.giveKit(g.getKit());
					return;
				}

				if (simple) {
					viewer.getMatchData().setGametype(g);
				} else {
					viewer.getMatchData().setKit(g.getKit());
				}

				viewer.openMenu(menu);
			});
		}

		return true;
	}
}
