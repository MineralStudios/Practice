package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;

public class CatagoryManager {
	final static FileConfiguration config = new FileConfiguration("catagory.yml", "plugins/Practice");

	static GlueList<Catagory> list = new GlueList<>();

	public static void registerCatagory(Catagory catagory) {
		list.add(catagory);
	}

	public static void remove(Catagory catagory) {
		list.remove(catagory);

		for (Queuetype queuetype : QueuetypeManager.getQueuetypes()) {
			queuetype.getCatagories().removeInt(catagory);
		}
	}

	public boolean contains(Catagory catagory) {
		for (Catagory g : list) {
			if (g.equals(catagory)) {
				return true;
			}
		}
		return false;
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static GlueList<Catagory> getCatagorys() {
		return list;
	}

	public static Catagory getCatagoryByName(String string) {
		for (Catagory g : list) {
			if (g.getName().equalsIgnoreCase(string)) {
				return g;
			}
		}
		return null;
	}

	public void save() {

		for (Catagory catagory : getCatagorys()) {
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

			registerCatagory(catagory);
		}
	}

	public static void setDefaults() {
		Catagory catagory = new Catagory("Defualt");
		catagory.setDefaults();
		registerCatagory(catagory);
	}
}
