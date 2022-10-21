package gg.mineral.practice.managers;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.FileConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;

public class ArenaManager {
	final static FileConfiguration config = new FileConfiguration("arenas.yml", "plugins/Practice");
	final static GlueList<Arena> list = new GlueList<>();

	static {
		load();
	}

	public static void register(Arena arena) {
		list.add(arena);
	}

	public static void remove(Arena arena) {
		list.remove(arena);

		for (Gametype gametype : GametypeManager.list()) {
			gametype.getArenas().removeBoolean(arena);
		}

		for (Queuetype queuetype : QueuetypeManager.list()) {
			queuetype.getArenas().removeBoolean(arena);
		}
	}

	public static FileConfiguration getConfig() {
		return config;
	}

	public static List<Arena> list() {
		return list;
	}

	public static Arena getByName(String string) {
		for (Arena arena : list()) {
			if (!arena.getName().equalsIgnoreCase(string)) {
				continue;
			}

			return arena;
		}

		return null;
	}

	public void save() {

		for (Arena arena : list()) {
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

			register(arena);
		}
	}

	public static void setDefaults() {
		Arena arena = new Arena("Default");
		arena.setDefaults();
		register(arena);
	}
}
