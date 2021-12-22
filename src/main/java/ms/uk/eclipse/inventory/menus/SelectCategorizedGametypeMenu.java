package ms.uk.eclipse.inventory.menus;

import org.bukkit.inventory.ItemStack;

import ms.uk.eclipse.core.utils.item.ItemBuilder;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.gametype.Catagory;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.queue.QueueEntry;
import ms.uk.eclipse.queue.QueueSearchTask;
import ms.uk.eclipse.queue.Queuetype;

public class SelectCategorizedGametypeMenu extends SelectGametypeMenu {
	Catagory c;

	public SelectCategorizedGametypeMenu(Queuetype q, Catagory c, boolean lore, boolean kitEditor) {
		super(q, lore, kitEditor);
		setTitle(new StrikingMessage(c.getName(), CC.PRIMARY, true));
		this.c = c;
	}

	@Override
	public void update() {
		for (Gametype g : c.getGametypes()) {
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

			setSlot(q.getGametypes().getInt(g), item, runnable);
		}
	}
}
