package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.gametype.Gametype;
import lombok.Getter;

public class GametypeManager {
	@Getter
	static FileConfiguration config = new FileConfiguration("gametype.yml", "plugins/Practice");
	@Getter
	final static Gametype[] gametypes = new Gametype[0];
	public static byte CURRENT_ID = 0;

	public static void registerGametype(Gametype gametype) {
		gametypes[gametype.getId()] = gametype;
	}

	public static void remove(Gametype gametype) {
		gametypes[gametype.getId()] = null;
		gametype.delete();
	}

	@Nullable
	public static Gametype getGametypeByName(String string) {
		for (int i = 0; i < gametypes.length; i++) {
			Gametype g = gametypes[i];
			if (g.getName().equalsIgnoreCase(string))
				return g;
		}

		return null;
	}

	public void save() {

		for (Gametype gametype : getGametypes())
			gametype.save();

		config.save();

	}

	public static void load() {
		ConfigurationSection configSection = getConfig().getConfigurationSection("Gametype.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (String key : configSection.getKeys(false)) {

			if (key == null)
				continue;

			Gametype gametype = new Gametype(key, CURRENT_ID++);

			gametype.load();

			registerGametype(gametype);
		}

		EloManager.setAllEloAndLeaderboards();
	}

	public static void setDefaults() {
		Gametype gametype = new Gametype("Default", CURRENT_ID++);
		gametype.setDefaults();
		registerGametype(gametype);
	}
}
