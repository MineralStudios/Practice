package gg.mineral.practice.managers;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.FileConfiguration;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;

public class GametypeManager {
	final static FileConfiguration config = new FileConfiguration("gametype.yml", "plugins/Practice");
	final static GlueList<Gametype> list = new GlueList<>();

	static {
		load();
	}

	public static void register(Gametype gametype) {
		list.add(gametype);
	}

	public static void remove(Gametype gametype) {
		list.remove(gametype);

		for (Catagory catagory : CatagoryManager.list()) {
			catagory.getGametypeMap().remove(gametype);
		}

		for (Queuetype queuetype : QueuetypeManager.list()) {
			queuetype.getGametypeMap().removeInt(gametype);
		}
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static List<Gametype> list() {
		return list;
	}

	public static Gametype getByName(String string) {
		for (Gametype gametype : list()) {
			if (!gametype.getName().equalsIgnoreCase(string)) {
				continue;
			}

			return gametype;
		}

		return null;
	}

	public static void save() {

		for (Gametype gametype : list()) {
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

			register(gametype);
		}
	}

	public static void setDefaults() {
		Gametype gametype = new Gametype("Default");
		gametype.setDefaults();
		register(gametype);
	}
}
