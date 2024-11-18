package gg.mineral.practice.managers;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.catagory.Catagory;
import lombok.Getter;
import lombok.val;

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

		for (val queuetype : QueuetypeManager.getQueuetypes().values())
			queuetype.getMenuEntries().removeInt(catagory);
	}

	public boolean contains(Catagory catagory) {
		for (val c : catagories)
			if (c.equals(catagory))
				return true;

		return false;
	}

	public static Catagory getCatagoryByName(String string) {
		for (val catagory : catagories)
			if (catagory.getName().equalsIgnoreCase(string))
				return catagory;

		return null;
	}

	public void save() {

		for (val catagory : getCatagories())
			catagory.save();

		config.save();

	}

	public static void load() {
		val configSection = getConfig().getConfigurationSection("Catagory.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (val key : configSection.getKeys(false)) {

			if (key == null)
				continue;

			val catagory = new Catagory(key);

			catagory.load();

			registerCatagory(catagory);
		}
	}

	public static void setDefaults() {
		val catagory = new Catagory("Default");
		catagory.setDefaults();
		registerCatagory(catagory);
	}
}
