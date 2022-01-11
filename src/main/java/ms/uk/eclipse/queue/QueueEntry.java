package ms.uk.eclipse.queue;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import land.strafe.api.collection.GlueList;
import land.strafe.api.config.FileConfiguration;
import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.ChatMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.gametype.Gametype;
import ms.uk.eclipse.managers.PlayerManager;

public class QueueEntry {
	Queuetype q;
	Gametype g;
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	Object2ObjectOpenHashMap<UUID, ItemStack[]> customKits = new Object2ObjectOpenHashMap<>();

	public QueueEntry(Queuetype q, Gametype g) {
		this.q = q;
		this.g = g;
	}

	public boolean equals(QueueEntry qd) {
		return this.q.equals(qd.getQueuetype()) && this.g.equals(qd.getGametype());
	}

	public Queuetype getQueuetype() {
		return q;
	}

	public Gametype getGametype() {
		return g;
	}

	public ItemStack[] getCustomKit(Profile profile) {
		ItemStack[] kit = customKits.get(profile.getUUID());

		if (kit == null) {
			ConfigurationSection cs = playerManager.getConfig().getConfigurationSection(profile.getName() + ".KitData."
					+ getGametype().getName() + "." + getQueuetype().getName());

			if (cs == null) {
				return null;
			}

			GlueList<ItemStack> items = new GlueList<>();

			for (String key : cs.getKeys(false)) {
				Object o = cs.get(key);

				if (o == null) {
					items.add(null);
					continue;
				}

				if (o instanceof ItemStack) {
					items.add((ItemStack) o);
					continue;
				}

				items.add(null);
			}

			kit = items.toArray(new ItemStack[0]);
			customKits.put(profile.getUUID(), kit);
		}

		return kit;
	}

	public void saveKit(Profile profile) {
		ItemStack[] cont = profile.getInventory().getContents();

		customKits.put(profile.getUUID(), cont);
		FileConfiguration config = playerManager.getConfig();
		String path = profile.getName() + ".KitData." + getGametype().getName() + "."
				+ getQueuetype().getName() + ".";

		for (int f = 0; f < cont.length; f++) {
			ItemStack item = cont[f];

			if (item == null) {
				config.set(path + f, "empty");
				continue;
			}

			config.set(path + f, item);
		}

		config.save();

		profile.bukkit().closeInventory();
		profile.message(new ChatMessage("Your kit has been saved", CC.PRIMARY, false));
	}
}
