package gg.mineral.practice.queue;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

public class QueueEntry {
	@Getter
	Queuetype queuetype;
	@Getter
	Gametype gametype;

	Object2ObjectOpenHashMap<UUID, ItemStack[]> customKits = new Object2ObjectOpenHashMap<>();

	public QueueEntry(Queuetype queuetype, Gametype gametype) {
		this.queuetype = queuetype;
		this.gametype = gametype;
	}

	public boolean equals(QueueEntry queueEntry) {
		return this.queuetype.equals(queueEntry.getQueuetype()) && this.gametype.equals(queueEntry.getGametype());
	}

	public ItemStack[] getCustomKit(Profile profile) {
		ItemStack[] kit = customKits.get(profile.getUUID());

		if (kit == null) {
			ConfigurationSection cs = ProfileManager.getPlayerConfig()
					.getConfigurationSection(profile.getName() + ".KitData."
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
		FileConfiguration config = ProfileManager.getPlayerConfig();
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

		profile.getPlayer().closeInventory();
		ChatMessages.KIT_SAVED.send(profile.getPlayer());
	}
}
