package gg.mineral.practice.managers;

import org.bukkit.configuration.ConfigurationSection;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;

public class ArenaManager implements SaveableData {
	final FileConfiguration config = new FileConfiguration("arenas.yml", "plugins/Practice");
	final GametypeManager gametypeManager = PracticePlugin.INSTANCE.getGametypeManager();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final GlueList<Arena> list = new GlueList<>();

	public void registerArena(Arena arena) {
		list.add(arena);
	}

	public void remove(Arena arena) {
		list.remove(arena);

		for (Gametype gametype : gametypeManager.getGametypes()) {
			gametype.getArenas().remove(arena);
		}

		for (Queuetype queuetype : queuetypeManager.getQueuetypes()) {
			queuetype.getArenas().remove(arena);
		}
	}

	public boolean contains(Arena arena) {
		for (int i = 0; i < list.size(); i++) {
			Arena a = list.get(i);
			if (a.equals(arena)) {
				return true;
			}
		}

		return false;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	public GlueList<Arena> getArenas() {
		return list;
	}

	public Arena getArenaByName(String string) {
		for (int i = 0; i < list.size(); i++) {
			Arena a = list.get(i);
			if (a.getName().equalsIgnoreCase(string)) {
				return a;
			}
		}

		return null;
	}

	@Override
	public void save() {

		for (Arena arena : getArenas()) {
			arena.save();
		}

		config.save();
	}

	@Override
	public void load() {
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

	@Override
	public void setDefaults() {
		Arena arena = new Arena("Default");
		arena.setDefaults();
		registerArena(arena);
	}
}
