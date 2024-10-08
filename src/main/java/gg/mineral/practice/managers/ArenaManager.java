package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;
import lombok.Getter;

public class ArenaManager {
	@Getter
	final static FileConfiguration config = new FileConfiguration("arenas.yml", "plugins/Practice");
	@Getter
	final static Arena[] arenas = new Arena[0];
	public static byte CURRENT_ID = 0;

	public static void registerArena(Arena arena) {
		arenas[arena.getId()] = arena;
	}

	public static void remove(Arena arena) {
		arenas[arena.getId()] = null;
		arena.delete();

		for (Gametype gametype : GametypeManager.getGametypes())
			gametype.getArenas().remove(arena.getId());

		for (Queuetype queuetype : QueuetypeManager.getQueuetypes())
			queuetype.getArenas().remove(arena.getId());
	}

	public static Arena getArenaByName(String string) {
		for (int i = 0; i < arenas.length; i++) {
			Arena a = arenas[i];
			if (a.getName().equalsIgnoreCase(string))
				return a;
		}

		return null;
	}

	public static void save() {

		for (Arena arena : getArenas())
			arena.save();

		config.save();
	}

	public static void load() {
		ConfigurationSection configSection = getConfig().getConfigurationSection("Arena.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (String key : configSection.getKeys(false)) {

			if (key == null)
				continue;

			Arena arena = new Arena(key, CURRENT_ID++);

			arena.load();

			registerArena(arena);
		}
	}

	public static void setDefaults() {
		Arena arena = new Arena("Default", CURRENT_ID++);
		arena.setDefaults();
		registerArena(arena);
	}
}
