package gg.mineral.practice.inventory.menus;

import java.sql.SQLException;

import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.QueueEntryManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.queue.QueueEntry;
import gg.mineral.practice.queue.QueueSearchTask;
import gg.mineral.practice.queue.Queuetype;

public class SelectCategorizedGametypeMenu extends SelectGametypeMenu {
	Catagory c;

	public SelectCategorizedGametypeMenu(Queuetype q, Catagory c, boolean lore, boolean kitEditor) {
		super(q, lore, kitEditor);
		setTitle(CC.BLUE + c.getName());
		this.c = c;
	}

	@Override
	public MineralInventory build(Profile profile) {
		for (Gametype gametype : c.getGametypeMap()) {
			ItemBuilder itemBuild = new ItemBuilder(gametype.getDisplayItem())
					.name(gametype.getDisplayName());
			if (lore) {
				int InGame = 0;
				for (Match match : MatchManager.list()) {
					QueueEntry queueEntry = match.getData().getQueueEntry();

					if (queueEntry == null) {
						continue;
					}

					if (queueEntry.getGametype().equals(gametype) && queueEntry.getQueuetype().equals(queuetype)) {
						InGame++;
					}
				}
				itemBuild.lore(CC.ACCENT + "In Queue: " + QueueSearchTask.getNumberInQueue(queuetype, gametype),
						CC.ACCENT + "In Game: " + InGame);
			} else {
				itemBuild.lore();
			}
			ItemStack item = itemBuild.build();

			Runnable runnable = () -> {
				QueueEntry qe = QueueEntryManager.getOrCreate(queuetype, gametype);

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

			set(queuetype.getGametypeMap().getInt(gametype), item, runnable);
		}

		return true;
	}
}
