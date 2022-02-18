package ms.uk.eclipse.inventory.menus;

import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;

import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.PracticeMenu;
import ms.uk.eclipse.managers.MatchManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueueEntryManager;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.queue.QueueEntry;
import ms.uk.eclipse.queue.QueueSearchTask;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.tasks.MenuTask;

public class SelectGametypeMenu extends PracticeMenu {
	MatchManager matchManager = PracticePlugin.INSTANCE.getMatchManager();
	Queuetype q;
	boolean lore = false;
	boolean kitEditor;
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final QueueEntryManager queueEntryManager = PracticePlugin.INSTANCE.getQueueEntryManager();

	public SelectGametypeMenu(Queuetype q, boolean lore, boolean kitEditor) {
		super(CC.BLUE + q.getDisplayName());
		this.lore = lore;
		this.kitEditor = kitEditor;
		this.q = q;
		setClickCancelled(true);
	}

	@Override
	public boolean update() {

		for (Entry<Gametype, Integer> entry : q.getGametypes().object2IntEntrySet()) {

			Gametype g = entry.getKey();

			if (g.isInCatagory()) {
				continue;
			}

			ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem())
					.name(g.getDisplayName());

			if (lore) {
				int InGame = 0;
				for (Match match : matchManager.getMatchs()) {
					QueueEntry queueEntry = match.getData().getQueueEntry();

					if (queueEntry == null) {
						continue;
					}

					if (queueEntry.getGametype().equals(g) && queueEntry.getQueuetype().equals(q)) {
						InGame++;
					}
				}
				itemBuild.lore(CC.ACCENT + "In Queue: " + QueueSearchTask.getNumberInQueue(q, g),
						CC.ACCENT + "In Game: " + InGame);
			} else {
				itemBuild.lore();
			}
			ItemStack item = itemBuild.build();

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					QueueEntry qe = queueEntryManager.newEntry(q, g);

					if (kitEditor) {
						viewer.sendPlayerToKitEditor(qe);
						return;
					}

					viewer.addPlayerToQueue(qe);
				}
			};

			setSlot(entry.getValue(), item, runnable);
		}

		for (Entry<Catagory, Integer> entry : q.getCatagories().object2IntEntrySet()) {
			Catagory c = entry.getKey();
			ItemBuilder itemBuild = new ItemBuilder(c.getDisplayItem())
					.name(c.getDisplayName());
			itemBuild.lore();
			ItemStack item = itemBuild.build();
			setSlot(entry.getValue(), item, new MenuTask(new SelectCategorizedGametypeMenu(q, c, true, kitEditor)));
		}

		return true;
	}
}
