package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.queue.Queuetype;
import lombok.Getter;

public class QueuetypeManager {
	@Getter
	final static FileConfiguration config = new FileConfiguration("queues.yml", "plugins/Practice");
	@Getter
	static Queuetype[] queuetypes = new Queuetype[0];
	public static byte CURRENT_ID = 0;

	public static void registerQueuetype(Queuetype queuetype) {
		resizeQueuetypes();
		queuetypes[queuetype.getId()] = queuetype;
	}

	private static void resizeQueuetypes() {
		if (CURRENT_ID < queuetypes.length)
			return;
		Queuetype[] newQueuetypes = new Queuetype[queuetypes.length * 2];
		System.arraycopy(queuetypes, 0, newQueuetypes, 0, queuetypes.length);
		queuetypes = newQueuetypes;
	}

	public static void remove(Queuetype queuetype) {
		queuetypes[queuetype.getId()] = null;
		queuetype.delete();
	}

	@Nullable
	public static Queuetype getQueuetypeByName(String string) {
		for (int i = 0; i < queuetypes.length; i++) {
			Queuetype q = queuetypes[i];
			if (q.getName().equalsIgnoreCase(string))
				return q;
		}

		return null;
	}

	public void save() {

		for (Queuetype queuetype : getQueuetypes())
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
