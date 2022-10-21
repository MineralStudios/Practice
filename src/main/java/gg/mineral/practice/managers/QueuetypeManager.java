package gg.mineral.practice.managers;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.FileConfiguration;
import gg.mineral.practice.queue.Queuetype;

public class QueuetypeManager {
	final static FileConfiguration config = new FileConfiguration("queues.yml", "plugins/Practice");
	final static GlueList<Queuetype> list = new GlueList<>();

	static {
		load();
	}

	public static void register(Queuetype queuetype) {
		list.add(queuetype);
	}

	public static void remove(Queuetype queuetype) {
		list.remove(queuetype);
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static List<Queuetype> list() {
		return list;
	}

	public static Queuetype getByName(String string) {
		for (Queuetype queuetype : list()) {
			if (!queuetype.getName().equalsIgnoreCase(string)) {
				continue;
			}

			return queuetype;
		}

		return null;
	}

	public static void save() {

		for (Queuetype queuetype : list()) {
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

			register(queuetype);
		}
	}

	public static void setDefaults() {
		Queuetype queuetype = new Queuetype("Default");
		queuetype.setDefaults();
		register(queuetype);
	}
}
