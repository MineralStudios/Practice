package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.PracticeMenu;

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
					.name(g.getDisplayName()).build();
			Runnable selectGametypeTask = () -> {
				if (viewer.getPlayerStatus() == PlayerStatus.KIT_CREATOR) {
					viewer.giveKit(g.getKit());
					return;
				}

				if (simple) {
					viewer.getMatchData().setGametype(g);
				} else {
					viewer.getMatchData().setKit(g.getKit(), g.getName());
				}

				viewer.openMenu(menu);
			};
			add(item, selectGametypeTask);
		}

		return true;
	}
}
