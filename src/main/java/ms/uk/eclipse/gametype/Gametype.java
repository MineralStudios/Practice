package ms.uk.eclipse.gametype;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import land.strafe.api.collection.GlueList;
import land.strafe.api.config.FileConfiguration;
import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.arena.Arena;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.kit.Kit;
import ms.uk.eclipse.managers.ArenaManager;
import ms.uk.eclipse.managers.CatagoryManager;
import ms.uk.eclipse.managers.LeaderboardManager;
import ms.uk.eclipse.managers.QueuetypeManager;
import ms.uk.eclipse.queue.Queuetype;
import ms.uk.eclipse.util.SaveableData;

public class Gametype implements SaveableData {
	final FileConfiguration config = PracticePlugin.INSTANCE.getGametypeManager().getConfig();
	final QueuetypeManager queuetypeManager = PracticePlugin.INSTANCE.getQueuetypeManager();
	final ArenaManager arenaManager = PracticePlugin.INSTANCE.getArenaManager();
	Boolean regeneration;
	ItemStack displayItem;
	String displayName;
	final String name;
	int noDamageTicks;
	Boolean deadlyWater;
	Boolean griefing;
	Boolean build;
	Boolean looting;
	Boolean damage;
	Boolean hunger;
	Boolean inCatagory;
	Integer pearlCooldown;
	Object2BooleanOpenHashMap<Arena> arenas = new Object2BooleanOpenHashMap<>();
	Arena eventArena;
	boolean event = false;
	Kit kit;
	String path;
	Map<String, Integer> leaderboardMap;
	Catagory catagory;
	final LeaderboardManager leaderboardManager = PracticePlugin.INSTANCE.getLeaderboardManager();
	final CatagoryManager catagoryManager = PracticePlugin.INSTANCE.getCatagoryManager();

	public Gametype(String name) {
		this.name = name;
		this.path = "Gametype." + getName() + ".";
	}

	public boolean getRegeneration() {
		return regeneration;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getName() {
		return name;
	}

	public int getNoDamageTicks() {
		return noDamageTicks;
	}

	public Boolean getDeadlyWater() {
		return deadlyWater;
	}

	public Boolean getGriefing() {
		return griefing;
	}

	public Boolean getBuild() {
		return build;
	}

	public Boolean getLooting() {
		return looting;
	}

	public Boolean getDamage() {
		return damage;
	}

	public Boolean getHunger() {
		return hunger;
	}

	public Integer getPearlCooldown() {
		return pearlCooldown;
	}

	public boolean isInCatagory() {
		return inCatagory;
	}

	public Object2BooleanOpenHashMap<Arena> getArenas() {
		return arenas;
	}

	public List<Arena> getEnabledArenas() {
		List<Arena> arenaList = new GlueList<Arena>();

		for (Entry<Arena, Boolean> entry : arenas.object2BooleanEntrySet()) {
			if (entry.getValue()) {
				arenaList.add(entry.getKey());
			}
		}

		return arenaList;
	}

	public Kit getKit() {
		return kit;
	}

	public void setRegeneration(Boolean Regeneration) {
		this.regeneration = Regeneration;
		save();
	}

	public void addToCatagory(Catagory c) {
		c.addGametype(this);
		save();
	}

	public void removeFromCatagory(Catagory c) {
		c.removeGametype(this);
		save();
	}

	public void setDisplayItem(ItemStack DisplayItem) {
		this.displayItem = DisplayItem;
		save();
	}

	public void setDisplayName(String DisplayName) {
		this.displayName = DisplayName;
		save();
	}

	public void setNoDamageTicks(int i) {
		this.noDamageTicks = i;
		save();
	}

	public void setDeadlyWater(Boolean DeadlyWater) {
		this.deadlyWater = DeadlyWater;
		save();
	}

	public void setGriefing(Boolean Griefing) {
		this.griefing = Griefing;
		save();
	}

	public void setBuild(Boolean Build) {
		this.build = Build;
		save();
	}

	public void setLooting(Boolean Looting) {
		this.looting = Looting;
		save();
	}

	public void setDamage(Boolean Damage) {
		this.damage = Damage;
		save();
	}

	public void setHunger(Boolean Hunger) {
		this.hunger = Hunger;
		save();
	}

	public void setPearlCooldown(Integer pearlCooldown) {
		this.pearlCooldown = pearlCooldown;
		save();
	}

	public void setSlot(Queuetype queuetype, int slot) {
		queuetype.getGametypes().put(this, slot);
		save();
	}

	public void addToQueuetype(Queuetype queuetype, int slot) {
		queuetype.getGametypes().put(this, slot);
		save();
	}

	public void removeFromQueuetype(Queuetype queuetype) {
		queuetype.getGametypes().remove(this);
		save();
	}

	public void enableArena(Arena arena, Boolean enabled) {
		arenas.put(arena, enabled);
		save();
	}

	public void setEventArena(Arena arena) {
		this.eventArena = arena;
		save();
	}

	public Arena getEventArena() {
		return eventArena;
	}

	public boolean getEvent() {
		return event;
	}

	public void setEvent(boolean b) {
		event = b;
		save();
	}

	public void setKit(Kit kit) {
		this.kit = kit;
		save();
	}

	public boolean equals(Gametype g) {
		return g.getName().equalsIgnoreCase(getName());
	}

	public void updatePlayerLeaderboard(Profile p, int elo) {
		leaderboardMap.put(p.getName(), elo);
		Map<String, Integer> sortedMap = sortByValue(leaderboardMap);
		if (sortedMap.size() > 10)
			sortedMap = reduceSize(sortedMap, 10);
		leaderboardMap = sortedMap;
		updateLeaderboard(p, elo);
	}

	private void updateLeaderboard(Profile p, int elo) {
		leaderboardManager.updateElo(p, this.getName(), elo);
	}

	private Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {
		List<Map.Entry<String, Integer>> list = new GlueList<Map.Entry<String, Integer>>(unsortMap.entrySet());
		Collections.reverseOrder();
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Object2IntLinkedOpenHashMap<String> sortedMap = new Object2IntLinkedOpenHashMap<String>();

		for (int i = 0; i < list.size(); i++) {
			Map.Entry<String, Integer> entry = list.get(i);
			sortedMap.put(entry.getKey(), entry.getValue());
		}

		return sortedMap;
	}

	public Map<String, Integer> reduceSize(Map<String, Integer> map, int size) {
		Map<String, Integer> reducedMap = new HashMap<String, Integer>();
		int i = 1;
		for (Entry<String, Integer> e : map.entrySet()) {
			if (size > i) {
				reducedMap.put(e.getKey(), e.getValue());
				i++;
			} else
				break;
		}
		return reducedMap;
	}

	public List<String> getLeaderboardLore() {
		List<String> lore = new GlueList<>();

		for (Map.Entry<String, Integer> entry : leaderboardMap.entrySet()) {
			lore.add(entry.getKey() + ": " + entry.getValue());
		}

		return lore;
	}

	public LeaderboardManager getLeaderboardManager() {
		return leaderboardManager;
	}

	@Override
	public void save() {
		config.set(path + "Regen", regeneration);
		config.set(path + "Event", event);
		if (eventArena != null) {
			config.set(path + "EventArena", eventArena.getName());
		}
		config.set(path + "PearlCooldown", pearlCooldown);
		config.set(path + "Hunger", hunger);
		config.set(path + "Damage", damage);
		config.set(path + "Looting", looting);
		config.set(path + "Build", build);
		config.set(path + "Griefing", griefing);
		config.set(path + "DeadlyWater", deadlyWater);
		config.set(path + "NoDamageTicks", noDamageTicks);
		config.set(path + "DisplayName", displayName);
		config.set(path + "DisplayItem", displayItem);
		config.set(path + "InCatagory", inCatagory);

		if (inCatagory) {
			config.set(path + "Catagory", catagory);
		}

		for (Queuetype q : queuetypeManager.getQueuetypes()) {
			boolean containsGametype = q.getGametypes().containsKey(this);
			config.set(path + q.getName() + ".Enabled", containsGametype);

			if (containsGametype) {
				config.set(path + q.getName() + ".Slot", q.getGametypes().getInt(this));
			}
		}

		for (Entry<Arena, Boolean> entry : arenas.object2BooleanEntrySet()) {
			config.set(path + "Arenas." + entry.getKey().getName(), entry.getValue());
		}

		ItemStack[] contents = kit.getContents();
		ItemStack[] armourContents = kit.getArmourContents();

		for (int i = 0; i < contents.length; i++) {
			ItemStack item = contents[i];

			if (item == null) {
				config.set(path + "Kit.Contents." + i, "empty");
				continue;
			}

			config.set(path + "Kit.Contents." + i, item);

		}

		for (int x = 0; x < armourContents.length; x++) {
			ItemStack armour = armourContents[x];

			if (armour == null) {
				config.set(path + "Kit.Armour." + x, "empty");
				continue;
			}

			config.set(path + "Kit.Armour." + x, armour);
		}
		config.save();
	}

	@Override
	public void load() {
		this.regeneration = config.getBoolean(path + "Regen", true);
		this.displayItem = config.getItemstack(path + "DisplayItem", new ItemStack(Material.DIAMOND_SWORD));
		this.displayName = config.getString(path + "DisplayName", getName());
		this.noDamageTicks = config.getInt(path + "NoDamageTicks", 20);
		this.deadlyWater = config.getBoolean(path + "DeadlyWater", false);
		this.griefing = config.getBoolean(path + "Griefing", false);
		this.build = config.getBoolean(path + "Build", false);
		this.looting = config.getBoolean(path + "Looting", false);
		this.damage = config.getBoolean(path + "Damage", true);
		this.hunger = config.getBoolean(path + "Hunger", true);
		this.inCatagory = config.getBoolean(path + "InCatagory", false);
		this.event = config.getBoolean(path + "Event", false);
		this.eventArena = arenaManager.getArenaByName(config.getString(path + "EventArena", ""));

		if (inCatagory) {
			this.catagory = catagoryManager.getCatagoryByName(config.getString(path + "Catagory", null));

			if (catagory != null) {
				catagory.addGametype(this);
			}
		}

		this.pearlCooldown = config.getInt(path + "PearlCooldown", 10);

		for (Queuetype q : queuetypeManager.getQueuetypes()) {
			if (!config.getBoolean(path + q.getName() + ".Enabled", false)) {
				continue;
			}

			q.addGametype(this, config.getInt(path + q.getName() + ".Slot", 0));
		}

		for (Arena a : arenaManager.getArenas()) {
			if (config.getBoolean(path + "Arenas." + a.getName(), false)) {
				arenas.put(a, true);
			}
		}

		ConfigurationSection cs = config.getConfigurationSection(path + "Kit.Armour");

		List<ItemStack> armour = new GlueList<>();

		if (cs != null) {
			for (String key : cs.getKeys(false)) {
				Object o = cs.get(key);
				if (o instanceof ItemStack)
					armour.add((ItemStack) o);
				else
					armour.add(new ItemStack(Material.AIR));
			}
		}

		cs = config.getConfigurationSection(path + "Kit.Contents");

		List<ItemStack> items = new GlueList<>();

		if (cs != null) {
			for (String key : cs.getKeys(false)) {
				Object o = cs.get(key);
				if (o instanceof ItemStack)
					items.add((ItemStack) o);
				else
					items.add(new ItemStack(Material.AIR));
			}
		}

		this.kit = new Kit(items.toArray(new ItemStack[0]), armour.toArray(new ItemStack[0]));

		try {
			leaderboardMap = leaderboardManager.getLeaderboardMap(this.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
