package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;

public class GametypeManager {
	static FileConfiguration config = new FileConfiguration("gametype.yml", "plugins/Practice");

	static GlueList<Gametype> list = new GlueList<>();

	public static void registerGametype(Gametype gametype) {
		list.add(gametype);
	}

	public static void remove(Gametype gametype) {
		list.remove(gametype);

		for (Catagory catagory : CatagoryManager.getCatagorys()) {
			catagory.getGametypes().remove(gametype);
		}

		for (Queuetype queuetype : QueuetypeManager.getQueuetypes()) {
			queuetype.getGametypes().removeInt(gametype);
		}
	}

	public boolean contains(Gametype gametype) {
		for (Gametype g : list) {
			if (g.equals(gametype)) {
				return true;
			}
		}
		return false;
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static GlueList<Gametype> getGametypes() {
		return list;
	}

	public static Gametype getGametypeByName(String string) {
		for (Gametype g : list) {
			if (g.getName().equalsIgnoreCase(string)) {
				return g;
			}
		}
		return null;
	}

	public void save() {

		for (Gametype gametype : getGametypes()) {
			gametype.save();
		}

		config.save();

	}

	public static void load() {
		ConfigurationSection configSection = getConfig().getConfigurationSection("Gametype.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (String key : configSection.getKeys(false)) {

			if (key == null) {
				continue;
			}

			Gametype gametype = new Gametype(key);

			gametype.load();

			registerGametype(gametype);
		}
	}

	public static void setDefaults() {
		Gametype gametype = new Gametype("Default");
		gametype.setDefaults();
		registerGametype(gametype);
	}
}
