package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.queue.Queuetype;
import lombok.Getter;

public class QueuetypeManager {
	@Getter
	final static FileConfiguration config = new FileConfiguration("queues.yml", "plugins/Practice");
	@Getter
	final static GlueList<Queuetype> queuetypes = new GlueList<>();

	public static void registerQueuetype(Queuetype queuetype) {
		queuetypes.add(queuetype);
	}

	public static void remove(Queuetype queuetype) {
		queuetypes.remove(queuetype);
		queuetype.delete();
	}

	public static Queuetype getQueuetypeByName(String string) {
		for (int i = 0; i < queuetypes.size(); i++) {
			Queuetype q = queuetypes.get(i);
			if (q.getName().equalsIgnoreCase(string)) {
				return q;
			}
		}

		return null;
	}

	public void save() {

		for (Queuetype queuetype : getQueuetypes()) {
			queuetype.save();
		}

		config.save();
	}

	public static void load() {
		ConfigurationSection configSection = getConfig().getConfigurationSection("Queue.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (String key : configSection.getKeys(false)) {

			if (key == null) {
				continue;
			}

			Queuetype queuetype = new Queuetype(key);

			queuetype.load();

			registerQueuetype(queuetype);
		}
	}

	public static void setDefaults() {
		Queuetype queuetype = new Queuetype("Default");
		queuetype.setDefaults();
		registerQueuetype(queuetype);
	}
}
