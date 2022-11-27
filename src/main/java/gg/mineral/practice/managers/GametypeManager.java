package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;

public class GametypeManager implements SaveableData {
	FileConfiguration config = new FileConfiguration("gametype.yml", "plugins/Practice");
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();
	final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
	GlueList<Gametype> list = new GlueList<>();

	public void registerGametype(Gametype gametype) {
		list.add(gametype);
	}

	public void remove(Gametype gametype) {
		list.remove(gametype);

		for (Catagory catagory : catagoryManager.getCatagorys()) {
			catagory.getGametypes().remove(gametype);
		}

		for (Queuetype queuetype : queuetypeManager.getQueuetypes()) {
			queuetype.getGametypes().remove(gametype);
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

	public FileConfiguration getConfig() {
		return config;
	}

	public GlueList<Gametype> getGametypes() {
		return list;
	}

	public Gametype getGametypeByName(String string) {
		for (Gametype g : list) {
			if (g.getName().equalsIgnoreCase(string)) {
				return g;
			}
		}
		return null;
	}

	@Override
	public void save() {

		for (Gametype gametype : getGametypes()) {
			gametype.save();
		}

		config.save();

	}

	@Override
	public void load() {
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

	@Override
	public void setDefaults() {
		Gametype gametype = new Gametype("Default");
		gametype.setDefaults();
		registerGametype(gametype);
	}
}
