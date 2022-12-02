package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.queue.Queuetype;

public class QueuetypeManager {
	final static FileConfiguration config = new FileConfiguration("queues.yml", "plugins/Practice");
	final static GlueList<Queuetype> list = new GlueList<>();

	public static void registerQueuetype(Queuetype queuetype) {
		list.add(queuetype);
	}

	public static void remove(Queuetype queuetype) {
		list.remove(queuetype);
	}

	public boolean contains(Queuetype queuetype) {
		for (int i = 0; i < list.size(); i++) {
			Queuetype q = list.get(i);
			if (q.equals(queuetype)) {
				return true;
			}
		}
		return false;
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static GlueList<Queuetype> getQueuetypes() {
		return list;
	}

	public static Queuetype getQueuetypeByName(String string) {
		for (int i = 0; i < list.size(); i++) {
			Queuetype q = list.get(i);
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
