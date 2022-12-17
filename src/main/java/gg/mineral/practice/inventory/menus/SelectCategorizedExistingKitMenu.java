package gg.mineral.practice.inventory.menus;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
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
			ItemStack item = new ItemBuilder(g.getDisplayItem())
					.name(g.getDisplayName()).lore().build();
			Runnable selectGametypeTask = () -> {
				if (viewer.getPlayerStatus() == PlayerStatus.KIT_CREATOR) {
					viewer.giveKit(g.getKit());
					return;
				}

				if (simple) {
					viewer.getMatchData().setGametype(g);
				} else {
					viewer.getMatchData().setKit(g.getKit());
				}

				viewer.openMenu(menu);
			};
			add(item, selectGametypeTask);
		}

		return true;
	}
}
