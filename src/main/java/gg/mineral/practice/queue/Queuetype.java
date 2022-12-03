package gg.mineral.practice.queue;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.gametype.Catagory;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.server.combat.KnockbackProfile;
import gg.mineral.server.combat.KnockbackProfileList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class Queuetype implements SaveableData {
	final FileConfiguration config = QueuetypeManager.getConfig();

	ItemStack displayItem;
	String name;
	String displayName;
	Integer slotNumber;
	Boolean ranked;
	final String path;
	KnockbackProfile knockback = null;
	Object2IntOpenHashMap<Gametype> gametypes = new Object2IntOpenHashMap<>();
	List<Arena> arenas = new GlueList<>();
	Object2IntOpenHashMap<Catagory> catagories = new Object2IntOpenHashMap<>();

	public Queuetype(String name) {
		this.name = name;
		this.path = "Queue." + getName() + ".";
	}

	List<Arena> arenaList = new GlueList<>();

	public synchronized Arena nextArena(Gametype g) {

		if (arenas.isEmpty()) {
			return null;
		}

		if (g.getArenas().isEmpty()) {
			return null;
		}

		if (arenaList.isEmpty()) {
			arenaList = new GlueList<>(arenas);
			arenaList.retainAll(g.getArenas());

			if (arenaList.isEmpty()) {
				return null;
			}
		}

		return arenaList.remove(0);
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

	public List<Arena> getArenas() {
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
		if (enabled) {
			arenas.add(arena);
		} else {
			arenas.remove(arena);
		}
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

		for (Arena arena : arenas) {
			config.set("Queue." + getName() + ".Arenas." + arena.getName(), true);
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

		for (int i = 0; i < ArenaManager.getArenas().size(); i++) {
			Arena a = ArenaManager.getArenas().get(i);

			if (config.getBoolean(path + "Arenas." + a.getName(), false)) {
				arenas.add(a);
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
