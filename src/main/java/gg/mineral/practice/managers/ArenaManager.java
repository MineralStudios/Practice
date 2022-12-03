package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;

public class ArenaManager {
	final static FileConfiguration config = new FileConfiguration("arenas.yml", "plugins/Practice");

	final static GlueList<Arena> list = new GlueList<>();

	public static void registerArena(Arena arena) {
		list.add(arena);
	}

	public static void remove(Arena arena) {
		list.remove(arena);

		for (Gametype gametype : GametypeManager.getGametypes()) {
			gametype.getArenas().remove(arena);
		}

		for (Queuetype queuetype : QueuetypeManager.getQueuetypes()) {
			queuetype.getArenas().remove(arena);
		}
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static GlueList<Arena> getArenas() {
		return list;
	}

	public static Arena getArenaByName(String string) {
		for (int i = 0; i < list.size(); i++) {
			Arena a = list.get(i);
			if (a.getName().equalsIgnoreCase(string)) {
				return a;
			}
		}

		return null;
	}

	public static void save() {

		for (Arena arena : getArenas()) {
			arena.save();
		}

		config.save();
	}

	public static void load() {
		ConfigurationSection configSection = getConfig().getConfigurationSection("Arena.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (String key : configSection.getKeys(false)) {

			if (key == null) {
				continue;
			}

			Arena arena = new Arena(key);

			arena.load();

			registerArena(arena);
		}
	}

	public static void setDefaults() {
		Arena arena = new Arena("Default");
		arena.setDefaults();
		registerArena(arena);
	}
}
