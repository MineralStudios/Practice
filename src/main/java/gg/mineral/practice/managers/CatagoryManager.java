package gg.mineral.practice.managers;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.FileConfiguration;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.queue.Queuetype;

public class CatagoryManager {
	final static FileConfiguration config = new FileConfiguration("catagory.yml", "plugins/Practice");
	final static GlueList<Catagory> list = new GlueList<>();

	static {
		load();
	}

	public static void register(Catagory catagory) {
		list.add(catagory);
	}

	public static void remove(Catagory catagory) {
		list.remove(catagory);

		for (Queuetype queuetype : QueuetypeManager.list()) {
			queuetype.getCatagories().removeInt(catagory);
		}
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static List<Catagory> list() {
		return list;
	}

	public static Catagory getByName(String string) {
		for (Catagory catagory : list) {
			if (!catagory.getName().equalsIgnoreCase(string)) {
				continue;
			}

			return catagory;
		}

		return null;
	}

	public static void save() {

		for (Catagory catagory : list()) {
			catagory.save();
		}

		config.save();

	}

	public static void load() {
		ConfigurationSection configSection = getConfig().getConfigurationSection("Catagory.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (String key : configSection.getKeys(false)) {

			if (key == null) {
				continue;
			}

			Catagory catagory = new Catagory(key);

			catagory.load();

			register(catagory);
		}
	}

	public static void setDefaults() {
		Catagory catagory = new Catagory("Defualt");
		catagory.setDefaults();
		register(catagory);
	}
}
