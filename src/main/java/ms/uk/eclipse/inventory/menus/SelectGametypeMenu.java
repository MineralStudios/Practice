package ms.uk.eclipse.inventory.menus;

import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.inventory.Menu;
import ms.uk.eclipse.managers.MatchManager;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.QueueEntryManager;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.queue.QueueEntry;
import ms.uk.eclipse.queue.QueueSearchTask;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.tasks.MenuTask;

public class SelectGametypeMenu extends Menu {
	MatchManager matchManager = PracticePlugin.INSTANCE.getMatchManager();
	Queuetype q;
	boolean lore = false;
	boolean kitEditor;
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	final QueueEntryManager queueEntryManager = PracticePlugin.INSTANCE.getQueueEntryManager();

	public SelectGametypeMenu(Queuetype q, boolean lore, boolean kitEditor) {
		super(new StrikingMessage(q.getDisplayName(), CC.PRIMARY, true));
		this.lore = lore;
		this.kitEditor = kitEditor;
		this.q = q;
		setClickCancelled(true);
	}

	public void update() {

		for (Entry<Gametype, Integer> entry : q.getGametypes().object2IntEntrySet()) {

			Gametype g = entry.getKey();

			if (g.isInCatagory()) {
				continue;
			}

			ItemBuilder itemBuild = new ItemBuilder(g.getDisplayItem())
					.name(new ChatMessage(g.getDisplayName(), CC.WHITE, true).toString());

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
				itemBuild.lore("In Queue: " + QueueSearchTask.getNumberInQueue(q, g), "In Game: " + InGame);
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
					.name(new ChatMessage(c.getDisplayName(), CC.WHITE, true).toString());
			itemBuild.lore();
			ItemStack item = itemBuild.build();
			setSlot(entry.getValue(), item, new MenuTask(new SelectCategorizedGametypeMenu(q, c, true, kitEditor)));
		}
	}
}
