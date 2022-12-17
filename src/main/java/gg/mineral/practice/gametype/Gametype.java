package gg.mineral.practice.gametype;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.catagory.Catagory;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.kit.Kit;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.EloManager;
import gg.mineral.practice.managers.GametypeManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.practice.util.collection.LeaderboardMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;

public class Gametype implements SaveableData {
	final FileConfiguration config = GametypeManager.getConfig();

	@Getter
	Boolean regeneration, deadlyWater, griefing, build, looting, damage, hunger, boxing, event = false;
	@Getter
	boolean inCatagory;
	@Getter
	ItemStack displayItem;
	@Getter
	String displayName;
	@Getter
	final String name;
	@Getter
	int noDamageTicks;
	Integer pearlCooldown;
	@Getter
	List<Arena> arenas = new GlueList<>();
	@Getter
	Arena eventArena;
	@Getter
	Kit kit;
	String path, catagoryName;
	LeaderboardMap leaderboardMap;
	Object2IntOpenHashMap<Profile> eloMap = new Object2IntOpenHashMap<>();

	public Gametype(String name) {
		this.name = name;
		this.path = "Gametype." + getName() + ".";
	}

	public Integer getElo(Profile profile) {
		Integer elo = eloMap.get(profile);

		return elo == null ? EloManager.get(this, profile.getUUID()).whenComplete((value, ex) -> {
			eloMap.put(profile, value);
		}).join() : elo;
	}

	public CompletableFuture<Object2IntOpenHashMap<UUID>> getEloMap(Profile... profiles) {
		return CompletableFuture.supplyAsync(() -> {
			Object2IntOpenHashMap<UUID> map = new Object2IntOpenHashMap<UUID>();

			for (Profile profile : profiles) {
				map.put(profile.getUUID(), getElo(profile));
			}

			return map;
		});
	}

	public void saveElo(Profile profile) {
		Integer elo = eloMap.get(profile);

		if (elo == null) {
			return;
		}

		EloManager.update(profile, getName(), elo);
	}

	public void setElo(int elo, Profile profile) {
		this.eloMap.put(profile, elo);

		EloManager.update(profile, getName(), elo);
	}

	public void setRegeneration(Boolean regeneration) {
		this.regeneration = regeneration;
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

	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
		save();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		save();
	}

	public void setNoDamageTicks(int i) {
		this.noDamageTicks = i;
		save();
	}

	public void setDeadlyWater(Boolean deadlyWater) {
		this.deadlyWater = deadlyWater;
		save();
	}

	public void setGriefing(Boolean griefing) {
		this.griefing = griefing;
		save();
	}

	public void setBuild(Boolean build) {
		this.build = build;
		save();
	}

	public void setLooting(Boolean looting) {
		this.looting = looting;
		save();
	}

	public void setDamage(Boolean damage) {
		this.damage = damage;
		save();
	}

	public void setHunger(Boolean hunger) {
		this.hunger = hunger;
		save();
	}

	public void setBoxing(Boolean boxing) {
		this.boxing = boxing;
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
		if (enabled) {
			arenas.add(arena);
		} else {
			arenas.remove(arena);
		}
		save();
	}

	public void setEventArena(Arena arena) {
		this.eventArena = arena;
		save();
	}

	public void setEvent(boolean b) {
		event = b;
		save();
	}

	public void setKit(Kit kit) {
		this.kit = kit;
		save();
	}

	public boolean equals(Gametype gametype) {
		return gametype.getName().equalsIgnoreCase(getName());
	}

	public void updatePlayerLeaderboard(Profile p, int elo, int oldElo) {
		if (oldElo == 1000) {
			leaderboardMap.putOrReplace(p.getName(), elo, oldElo);
			return;
		}

		leaderboardMap.replace(p.getName(), elo, oldElo);
	}

	public List<String> getLeaderboardLore() {
		List<String> lore = new GlueList<>();

		for (gg.mineral.practice.util.collection.LeaderboardMap.Entry entry : leaderboardMap.getEntries()) {
			lore.add(entry.getKey() + ": " + entry.getValue());
		}

		return lore;
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
		config.set(path + "Boxing", boxing);
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
			config.set(path + "Catagory", catagoryName);
		}

		for (Queuetype q : QueuetypeManager.getQueuetypes()) {
			boolean containsGametype = q.getGametypes().containsKey(this);
			config.set(path + q.getName() + ".Enabled", containsGametype);

			if (containsGametype) {
				config.set(path + q.getName() + ".Slot", q.getGametypes().getInt(this));
			}
		}

		for (Arena arena : arenas) {
			config.set(path + "Arenas." + arena.getName(), true);
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
		this.boxing = config.getBoolean(path + "Boxing", false);
		this.inCatagory = config.getBoolean(path + "InCatagory", false);
		this.event = config.getBoolean(path + "Event", false);
		this.eventArena = ArenaManager.getArenaByName(config.getString(path + "EventArena", ""));

		if (inCatagory) {
			this.catagoryName = config.getString(path + "Catagory", null);

			if (catagoryName != null) {
				Catagory catagory = CatagoryManager.getCatagoryByName(catagoryName);

				if (catagory != null) {
					catagory.addGametype(this);
				}
			}
		}

		this.pearlCooldown = config.getInt(path + "PearlCooldown", 10);

		for (Queuetype q : QueuetypeManager.getQueuetypes()) {
			if (!config.getBoolean(path + q.getName() + ".Enabled", false)) {
				continue;
			}

			q.addGametype(this, config.getInt(path + q.getName() + ".Slot", 0));
		}

		for (Arena a : ArenaManager.getArenas()) {
			if (config.getBoolean(path + "Arenas." + a.getName(), false)) {
				arenas.add(a);
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

		this.kit = new Kit(this.getName(), items.toArray(new ItemStack[0]), armour.toArray(new ItemStack[0]));
		this.leaderboardMap = EloManager.getLeaderboardMap(this);
	}

	@Override
	public void setDefaults() {
		this.regeneration = true;
		this.displayItem = new ItemStack(Material.DIAMOND_SWORD);
		this.displayName = getName();
		this.noDamageTicks = 20;
		this.deadlyWater = false;
		this.griefing = false;
		this.build = false;
		this.looting = false;
		this.damage = true;
		this.hunger = true;
		this.boxing = false;
		this.inCatagory = false;
		this.event = false;
		this.eventArena = null;
		this.pearlCooldown = 10;

		this.kit = new Kit(this.getName(), new ItemStack[0], new ItemStack[0]);
		this.leaderboardMap = new LeaderboardMap();
	}
}
