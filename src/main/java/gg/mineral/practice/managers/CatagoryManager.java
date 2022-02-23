package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.SaveableData;
import land.strafe.api.collection.GlueList;
import land.strafe.api.config.FileConfiguration;

public class CatagoryManager implements SaveableData {
	final FileConfiguration config = new FileConfiguration("catagory.yml", "plugins/Practice");
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	GlueList<Catagory> list = new GlueList<>();

	public void registerCatagory(Catagory catagory) {
		list.add(catagory);
	}

	public void remove(Catagory catagory) {
		list.remove(catagory);

		for (Queuetype queuetype : queuetypeManager.getQueuetypes()) {
			queuetype.getCatagories().remove(catagory);
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

	public FileConfiguration getConfig() {
		return config;
	}

	public GlueList<Catagory> getCatagorys() {
		return list;
	}

	public Catagory getCatagoryByName(String string) {
		for (Catagory g : list) {
			if (g.getName().equalsIgnoreCase(string)) {
				return g;
			}
		}
		return null;
	}

	@Override
	public void save() {

		for (Catagory catagory : getCatagorys()) {
			catagory.save();
		}

		config.save();

	}

	@Override
	public void load() {
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

	@Override
	public void setDefaults() {
		Catagory catagory = new Catagory("Defualt");
		catagory.setDefaults();
		registerCatagory(catagory);
	}
}
