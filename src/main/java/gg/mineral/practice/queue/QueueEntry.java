package gg.mineral.practice.queue;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.ProfileManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
	Object2ObjectOpenHashMap<UUID, Int2ObjectOpenHashMap<ItemStack[]>> customKits = new Object2ObjectOpenHashMap<>();

	public boolean equals(QueueEntry queueEntry) {
		return this.queuetype.equals(queueEntry.getQueuetype()) && this.gametype.equals(queueEntry.getGametype());
	}

	private ItemStack[] getCustomKit(Profile profile, ConfigurationSection cs) {

		ItemStack[] kit = getGametype().getKit().getContents().clone();

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

		return kit;
	}

	public Int2ObjectOpenHashMap<ItemStack[]> getCustomKits(Profile profile) {
		Int2ObjectOpenHashMap<ItemStack[]> kitLoadouts = getCustomKits().get(profile.getUuid());

		if (kitLoadouts != null)
			return kitLoadouts;

		ConfigurationSection cs = ProfileManager.getPlayerConfig()
				.getConfigurationSection(profile.getName() + ".KitData."
						+ getGametype().getName() + "." + getQueuetype().getName());

		if (cs == null)
			return null;

		kitLoadouts = new Int2ObjectOpenHashMap<>();

		for (String key : cs.getKeys(false)) {
			ConfigurationSection cs1 = ProfileManager.getPlayerConfig()
					.getConfigurationSection(profile.getName() + ".KitData."
							+ getGametype().getName() + "." + getQueuetype().getName() + "." + key);

			if (cs1 == null)
				continue;

			kitLoadouts.put((int) Integer.valueOf(key), getCustomKit(profile, cs1));
		}

		getCustomKits().put(profile.getUuid(), kitLoadouts);

		return kitLoadouts;
	}
}
