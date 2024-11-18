package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.queue.QueuetypeMenuEntry;
import gg.mineral.practice.util.messages.CC;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;

@ClickCancelled(true)
public class SelectCategorizedGametypeMenu extends SelectGametypeMenu {
	protected final Catagory catagory;

	public SelectCategorizedGametypeMenu(Queuetype queuetype, Catagory catagory, Type type) {
		super();
		this.catagory = catagory;
		this.queuetype = queuetype;
		this.type = type;
		this.menuEntries = setMenuEntries();
	}

	@Override
	protected Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> setMenuEntries() {
		Object2IntLinkedOpenHashMap<QueuetypeMenuEntry> menuEntries = new Object2IntLinkedOpenHashMap<>();
		catagory.getGametypes().forEach(gametype -> {
			if (gametype.isInCatagory())
				menuEntries.put(gametype, queuetype.getMenuEntries().getInt(gametype));
		});
		return menuEntries;
	}

	@Override
	public String getTitle() {
		return CC.BLUE + catagory.getName();
	}

	@Override
	protected boolean shouldSkip(QueuetypeMenuEntry menuEntry) {
		return menuEntry instanceof Gametype gametype && !gametype.isInCatagory();
	}

	@Override
	public void onClose() {
		if (viewer.getPlayerStatus() == PlayerStatus.FIGHTING || viewer.getQueueSettings().isArenaSelection())
			return;

		viewer.openMenu(new SelectGametypeMenu(queuetype, type));
	}
}
