package gg.mineral.practice.inventory.menus;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.QueueEntryManager;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class SelectCategorizedGametypeMenu extends SelectGametypeMenu {
	Catagory c;
	boolean bots = false;

	public SelectCategorizedGametypeMenu(Queuetype q, Catagory c, Type type) {
		super(q, type);
		setTitle(CC.BLUE + c.getName());
		this.c = c;
	}

	@Override
	public boolean update() {
		for (Gametype g : c.getGametypes()) {
			ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem().clone())
					.name(g.getDisplayName());

			QueueEntry queueEntry = QueueEntryManager.newEntry(queuetype, g);

			if (type == Type.QUEUE) {
				List<QueueEntry> queueEntries = QueueSearchTask.getQueueEntries(viewer);

				if (queueEntries != null && queueEntries.contains(queueEntry)) {
					itemBuild.lore(CC.RED + "Click to leave queue.");
				} else {

					itemBuild.lore(CC.ACCENT + "In Queue: " + QueueSearchTask.getNumberInQueue(queuetype, g),
							CC.ACCENT + "In Game: " + MatchManager.getInGameCount(queuetype, g));
				}
			} else {
				itemBuild.lore();
			}
			ItemStack item = itemBuild.build();

			setSlot(queuetype.getGametypes().getInt(g), item, () -> {

				if (type == Type.KIT_EDITOR) {
					viewer.getPlayer().closeInventory();
					viewer.sendToKitEditor(queueEntry);
					return;
				}

				List<QueueEntry> queueEntries = QueueSearchTask.getQueueEntries(viewer);

				if (queueEntries != null && queueEntries.contains(queueEntry)) {
					viewer.removeFromQueue(queueEntry);
				} else {
					if (queueEntry.getGametype().getBotsEnabled() && queueEntry.getQueuetype().getBotsEnabled()) {
						this.bots = true;
						viewer.openMenu(new SelectOpponentTypeMenu(queueEntry, this));
					} else {
						viewer.addPlayerToQueue(queueEntry);
					}
				}

				if (viewer.getPlayerStatus() == PlayerStatus.QUEUEING) {
					reload();
				}
			});
		}

		return true;
	}

	@Override
	public void onClose() {
		if (bots)
			return;
		viewer.openMenu(new SelectGametypeMenu(queuetype, type));
	}
}
