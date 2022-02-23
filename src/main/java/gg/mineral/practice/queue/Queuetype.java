package gg.mineral.practice.queue;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.util.SaveableData;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;

public class Queuetype implements SaveableData {
	final FileConfiguration config = PracticePlugin.INSTANCE.getQueuetypeManager().getConfig();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();
	ItemStack displayItem;
	String name;
	String displayName;
	Integer slotNumber;
	Boolean ranked;
	final String path;
	KnockbackProfile knockback = null;
	Object2IntOpenHashMap<Gametype> gametypes = new Object2IntOpenHashMap<>();
	Object2BooleanOpenHashMap<Arena> arenas = new Object2BooleanOpenHashMap<>();
	Object2IntOpenHashMap<Catagory> catagories = new Object2IntOpenHashMap<>();

	public Queuetype(String name) {
		this.name = name;
		this.path = "Queue." + getName() + ".";
	}

	Iterator<Entry<Arena>> arenaIterator;

	public synchronized Arena nextArena(Gametype g) {

		if (arenas.isEmpty()) {
			return null;
		}

		if (g.getArenas().isEmpty()) {
			return null;
		}

		if (arenaIterator == null) {
			arenaIterator = arenas.object2BooleanEntrySet().fastIterator();
		}

		Arena arena = null;

		int iterations = 0;

		while (arenaIterator.hasNext()) {
			iterations++;

			Entry<Arena> entry = arenaIterator.next();

			if (entry.getBooleanValue()) {
				arena = entry.getKey();
				break;
			}

			if (iterations >= arenas.size()) {
				return null;
			}
		}

		if (!arenaIterator.hasNext()) {
			arenaIterator = arenas.object2BooleanEntrySet().fastIterator();
			return nextArena(g);
		}

		if (arena == null) {
			return null;
		}

		if (g.getArenas().keySet().contains(arena)) {
			return arena;
		}

		return nextArena(g);
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public Integer getSlotNumber() {
		return slotNumber;
	}

	public Object2BooleanOpenHashMap<Arena> getArenas() {
		return arenas;
	}

	public KnockbackProfile getKnockback() {
		return knockback;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		save();
	}

	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
		save();
	}

	public void setSlotNumber(Integer slotNumber) {
		this.slotNumber = slotNumber;
		save();
	}

	public void setRanked(boolean r) {
		this.ranked = r;
		save();
	}

	public void setKnockback(KnockbackProfile kb) {
		this.knockback = kb;
		save();
	}

	public void enableArena(Arena arena, Boolean enabled) {
		arenas.put(arena, enabled);
		save();
	}

	public boolean equals(Queuetype q) {
		return q.getName().equalsIgnoreCase(getName());
	}

	public Object2IntOpenHashMap<Gametype> getGametypes() {
		return gametypes;
	}

	public boolean isRanked() {
		return ranked;
	}

	public void addCatagory(Catagory catagory, int slot) {
		catagories.put(catagory, slot);
		save();
	}

	public void addGametype(Gametype gametype, int slot) {
		gametypes.put(gametype, slot);
		save();
	}

	public Object2IntOpenHashMap<Catagory> getCatagories() {
		return catagories;
	}

	@Override
	public void save() {
		config.set("Queuetype." + getName() + ".DisplayName", displayName);
		config.set("Queue." + getName() + ".Elo", ranked);
		config.set("Queue." + getName() + ".Slot", slotNumber);
		config.set("Queue." + getName() + ".DisplayItem", displayItem);

		if (knockback != null) {
			config.set("Queue." + getName() + ".Knockback", knockback.getName());
		}

		for (Entry<Arena> entry : arenas.object2BooleanEntrySet()) {
			config.set("Queue." + getName() + ".Arenas." + entry.getKey().getName(), entry.getBooleanValue());
		}

		config.save();
	}

	@Override
	public void load() {
		this.displayItem = config.getItemstack(path + "DisplayItem",
				new ItemStack(Material.DIAMOND_SWORD));
		this.displayName = config.getString(path + "DisplayName", getName());
		this.slotNumber = config.getInt(path + "Slot", 8);
		this.ranked = config.getBoolean(path + "Elo", false);
		String kbprofile = config.getString(path + "Knockback", "");

		if (!kbprofile.isEmpty()) {
			this.knockback = KnockbackProfileList.getKnockbackProfileByName(kbprofile);
		}

		for (int i = 0; i < arenaManager.getArenas().size(); i++) {
			Arena a = arenaManager.getArenas().get(i);

			if (config.getBoolean(path + "Arenas." + a.getName(), false)) {
				arenas.put(a, true);
			}
		}
	}

	@Override
	public void setDefaults() {
		this.displayItem = new ItemStack(Material.DIAMOND_SWORD);
		this.displayName = getName();
		this.slotNumber = 8;
		this.ranked = false;
	}
}
