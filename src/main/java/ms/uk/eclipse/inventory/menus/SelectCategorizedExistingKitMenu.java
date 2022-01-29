package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.Menu;

public class SelectCategorizedExistingKitMenu extends SelectExistingKitMenu {
	Catagory c;

	public SelectCategorizedExistingKitMenu(Catagory c, Menu menu, boolean simple) {
		super(menu, simple);
		setTitle(new StrikingMessage(c.getName(), CC.PRIMARY, true));
		this.c = c;
	}

	@Override
	public boolean update() {
		for (Gametype g : c.getGametypes()) {
			ItemStack item = new ItemBuilder(g.getDisplayItem())
					.name(new ChatMessage(g.getDisplayName(), CC.WHITE, true).toString()).build();
			Thread selectGametypeTask = new Thread() {
				@Override
				public void run() {
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
				}
			};
			add(item, selectGametypeTask);
		}

		return true;
	}
}
