package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.queue.Queuetype;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import lombok.Getter;

public class QueuetypeManager {
	@Getter
	final static FileConfiguration config = new FileConfiguration("queues.yml", "plugins/Practice");
	@Getter
	static Byte2ObjectOpenHashMap<Queuetype> queuetypes = new Byte2ObjectOpenHashMap<>();
	public static byte CURRENT_ID = 0;

	public static void registerQueuetype(Queuetype queuetype) {
		queuetypes.put(queuetype.getId(), queuetype);
	}

	public static void remove(Queuetype queuetype) {
		queuetypes.remove(queuetype.getId());
		queuetype.delete();
	}

	@Nullable
	public static Queuetype getQueuetypeByName(String string) {
		for (Queuetype queuetype : queuetypes.values())
			if (queuetype.getName().equalsIgnoreCase(string))
				return queuetype;

		return null;
	}

	public void save() {
		for (Queuetype queuetype : getQueuetypes().values())
			queuetype.save();

		config.save();
	}

	public static void load() {
		ConfigurationSection configSection = getConfig().getConfigurationSection("Queue.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (String key : configSection.getKeys(false)) {

			if (key == null)
				continue;

			Queuetype queuetype = new Queuetype(key, CURRENT_ID++);

			queuetype.load();

			registerQueuetype(queuetype);
		}
	}

	public static void setDefaults() {
		Queuetype queuetype = new Queuetype("Default", CURRENT_ID++);
		queuetype.setDefaults();
		registerQueuetype(queuetype);
	}
}
