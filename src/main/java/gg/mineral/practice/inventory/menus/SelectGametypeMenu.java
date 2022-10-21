package gg.mineral.practice.inventory.menus;

import java.sql.SQLException;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.api.inventory.InventoryBuilder;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.QueueEntryManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.Queuetype;

public class SelectGametypeMenu implements InventoryBuilder {
	Queuetype queuetype;
	boolean lore = false, kitEditor;

	public SelectGametypeMenu(Queuetype q, boolean lore, boolean kitEditor) {
		super(CC.BLUE + q.getDisplayName());
		this.lore = lore;
		this.kitEditor = kitEditor;
		this.queuetype = q;
		setItemDragging(true);
	}

	@Override
	public MineralInventory build(Profile profile) {

		for (Entry<Gametype, Integer> entry : queuetype.getGametypeMap().object2IntEntrySet()) {

			Gametype g = entry.getKey();

			if (g.isInCatagory()) {
				continue;
			}

			ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem())
					.name(g.getDisplayName());

			if (lore) {
				int InGame = 0;
				for (Match match : MatchManager.list()) {
					QueueEntry queueEntry = match.getData().getQueueEntry();

					if (queueEntry == null) {
						continue;
					}

					if (queueEntry.getGametype().equals(g) && queueEntry.getQueuetype().equals(queuetype)) {
						InGame++;
					}
				}
				itemBuild.lore(CC.ACCENT + "In Queue: " + QueueSearchTask.getNumberInQueue(queuetype, g),
						CC.ACCENT + "In Game: " + InGame);
			} else {
				itemBuild.lore();
			}
			ItemStack item = itemBuild.build();

			Runnable runnable = () -> {
				QueueEntry qe = QueueEntryManager.getOrCreate(queuetype, g);

				if (kitEditor) {
					viewer.sendPlayerToKitEditor(qe);
					return;
				}

				try {
					viewer.addPlayerToQueue(qe);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			};

			set(entry.getValue(), item, runnable);
		}

		for (Entry<Catagory, Integer> entry : queuetype.getCatagories().object2IntEntrySet()) {
			Catagory c = entry.getKey();
			ItemBuilder itemBuild = new ItemBuilder(c.getDisplayItem())
					.name(c.getDisplayName());
			itemBuild.lore();
			ItemStack item = itemBuild.build();
			set(entry.getValue(), item,
					new MenuTask(new SelectCategorizedGametypeMenu(queuetype, c, true, kitEditor)));
		}

		return true;
	}
}
