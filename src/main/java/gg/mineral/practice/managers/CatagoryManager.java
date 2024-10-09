package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.queue.Queuetype;
import lombok.Getter;

public class CatagoryManager {
	@Getter
	final static FileConfiguration config = new FileConfiguration("catagory.yml", "plugins/Practice");
	@Getter
	static GlueList<Catagory> catagories = new GlueList<>();

	public static void registerCatagory(Catagory catagory) {
		catagories.add(catagory);
	}

	public static void remove(Catagory catagory) {
		catagories.remove(catagory);
		catagory.delete();

		for (Queuetype queuetype : QueuetypeManager.getQueuetypes().values())
			queuetype.getCatagories().removeInt(catagory);
	}

	public boolean contains(Catagory catagory) {
		for (Catagory g : catagories) {
			if (g.equals(catagory)) {
				return true;
			}
		}
		return false;
	}

	public static Catagory getCatagoryByName(String string) {
		for (Catagory g : catagories) {
			if (g.getName().equalsIgnoreCase(string)) {
				return g;
			}
		}
		return null;
	}

	public void save() {

		for (Catagory catagory : getCatagories()) {
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
		Catagory catagory = new Catagory("Default");
		catagory.setDefaults();
		registerCatagory(catagory);
	}
}
