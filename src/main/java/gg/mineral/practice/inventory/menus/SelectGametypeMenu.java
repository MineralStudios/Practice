package gg.mineral.practice.inventory.menus;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.QueueEntryManager;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;

public class SelectGametypeMenu extends PracticeMenu {

	public enum Type {
		QUEUE, KIT_EDITOR
	}

	Queuetype queuetype;
	Type type;

	public SelectGametypeMenu(Queuetype queuetype, Type type) {
		super(CC.BLUE + queuetype.getDisplayName());
		this.type = type;
		this.queuetype = queuetype;
		setClickCancelled(true);
	}

	@Override
	public boolean update() {

		for (Entry<Gametype, Integer> entry : queuetype.getGametypes().object2IntEntrySet()) {

			Gametype g = entry.getKey();

			if (g.isInCatagory()) {
				continue;
			}

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

			setSlot(entry.getValue(), item, () -> {

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

		for (Entry<Catagory, Integer> entry : queuetype.getCatagories().object2IntEntrySet()) {
			Catagory c = entry.getKey();
			ItemBuilder itemBuild = new ItemBuilder(c.getDisplayItem())
					.name(c.getDisplayName());
			itemBuild.lore();
			ItemStack item = itemBuild.build();
			setSlot(entry.getValue(), item, interaction -> {
				Profile p = interaction.getProfile();
				p.openMenu(new SelectCategorizedGametypeMenu(queuetype, c, type));
			});
		}

		return true;
	}
}
