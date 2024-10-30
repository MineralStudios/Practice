package gg.mineral.practice.managers;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.arena.Arena;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;

public class ArenaManager {
	@Getter
	final static FileConfiguration config = new FileConfiguration("arenas.yml", "plugins/Practice");
	@Getter
	final static Byte2ObjectOpenHashMap<Arena> arenas = new Byte2ObjectOpenHashMap<>();
	public static byte CURRENT_ID = 0;

	public static void registerArena(Arena arena) {
		arenas.put(arena.getId(), arena);
	}

	public static void remove(Arena arena) {
		arenas.remove(arena.getId());
		arena.delete();

		for (val gametype : GametypeManager.getGametypes().values())
			gametype.getArenas().remove(arena.getId());

		for (val queuetype : QueuetypeManager.getQueuetypes().values())
			queuetype.getArenas().remove(arena.getId());
	}

	public static Arena getArenaByName(String string) {
		for (val arena : arenas.values())
			if (arena.getName().equalsIgnoreCase(string))
				return arena;

		return null;
	}

	public static void save() {
		for (val arena : getArenas().values())
			arena.save();

		config.save();
	}

	public static void load() {
		val configSection = getConfig().getConfigurationSection("Arena.");

		if (configSection == null) {
			setDefaults();
			return;
		}

		for (val key : configSection.getKeys(false)) {

			if (key == null)
				continue;

			val arena = new Arena(key, CURRENT_ID++);

			arena.load();

			registerArena(arena);
		}
	}

	public static void setDefaults() {
		val arena = new Arena("Default", CURRENT_ID++);
		arena.setDefaults();
		registerArena(arena);
	}
}
