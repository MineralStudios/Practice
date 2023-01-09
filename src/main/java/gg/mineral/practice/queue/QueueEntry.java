package gg.mineral.practice.queue;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.ProfileManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueueEntry {
	@Getter
	final Queuetype queuetype;
	@Getter
	final Gametype gametype;
	@Getter
	Object2ObjectOpenHashMap<UUID, ItemStack[]> customKits = new Object2ObjectOpenHashMap<>();

	public boolean equals(QueueEntry queueEntry) {
		return this.queuetype.equals(queueEntry.getQueuetype()) && this.gametype.equals(queueEntry.getGametype());
	}

	public ItemStack[] getCustomKit(Profile profile) {
		ItemStack[] kit = getCustomKits().get(profile.getUUID());

		if (kit != null) {
			return kit;
		}

		ConfigurationSection cs = ProfileManager.getPlayerConfig()
				.getConfigurationSection(profile.getName() + ".KitData."
						+ getGametype().getName() + "." + getQueuetype().getName());

		if (cs == null) {
			return null;
		}

		kit = getGametype().getKit().getContents().clone();

		for (String key : cs.getKeys(false)) {
			Object o = cs.get(key);

			int index = Integer.valueOf(key);

			if (o == null) {
				kit[index] = null;
				continue;
			}

			if (o instanceof ItemStack) {
				kit[index] = (ItemStack) o;
				continue;
			}

			kit[index] = null;
		}

		getCustomKits().put(profile.getUUID(), kit);

		return kit;
	}
}
