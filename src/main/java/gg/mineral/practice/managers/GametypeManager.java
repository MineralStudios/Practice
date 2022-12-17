package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;
import lombok.Getter;

public class GametypeManager {
	@Getter
	static FileConfiguration config = new FileConfiguration("gametype.yml", "plugins/Practice");
	@Getter
	static GlueList<Gametype> gametypes = new GlueList<>();

	public static void registerGametype(Gametype gametype) {
		gametypes.add(gametype);
	}

	public static void remove(Gametype gametype) {
		gametypes.remove(gametype);

		for (Catagory catagory : CatagoryManager.getCatagories()) {
			catagory.getGametypes().remove(gametype);
		}

		for (Queuetype queuetype : QueuetypeManager.getQueuetypes()) {
			queuetype.getGametypes().removeInt(gametype);
		}
	}

	public boolean contains(Gametype gametype) {
		for (Gametype g : gametypes) {
			if (g.equals(gametype)) {
				return true;
			}
		}
		return false;
	}

	public static Gametype getGametypeByName(String string) {
		for (Gametype g : gametypes) {
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
