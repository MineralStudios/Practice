package gg.mineral.practice.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.world.VoidWorldGenerator;
import gg.mineral.practice.util.world.WorldUtil;
import lombok.Getter;

public class Arena implements SaveableData {
	final FileConfiguration config = ArenaManager.getConfig();
	@Getter
	String name, path, displayName;
	@Getter
	Location location1, location2, waitingLocation;
	@Getter
	ItemStack displayItem;
	int currentNameID = 0;
	World world;
	@Getter
	private final byte id;

	public Arena(String name, byte id) {
		this.name = name;
		this.id = id;
		this.path = "Arena." + getName() + ".";
	}

	public void setLocation1(Location location1) {
		this.location1 = location1;
		save();
	}

	public void setLocation2(Location location2) {
		this.location2 = location2;
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

	public boolean equals(Arena a) {
		return a.getName().equalsIgnoreCase(getName());
	}

	public void setWaitingLocation(Location waitingLocation) {
		this.waitingLocation = waitingLocation;
		save();
	}

	public synchronized World generate() {
		World targetw = Bukkit
				.createWorld(new WorldCreator(getName() + "_" + currentNameID).generator(new VoidWorldGenerator()));
		WorldUtil.copyWorld(this.world.getWorldFolder(), targetw.getWorldFolder());
		currentNameID++;
		return targetw;
	}

	@Override
	public void save() {
		config.set(path + "Spawn.Waiting.x", waitingLocation.getBlockX());
		config.set(path + "Spawn.Waiting.y", waitingLocation.getBlockY());
		config.set(path + "Spawn.Waiting.z", waitingLocation.getBlockZ());
		config.set(path + "Spawn.World", waitingLocation.getWorld().getName());
		config.set(path + "DisplayName", name);
		config.set(path + "DisplayItem", displayItem);
		config.set(path + "Spawn.1.x", location1.getBlockX());
		config.set(path + "Spawn.1.y", location1.getBlockY());
		config.set(path + "Spawn.1.z", location1.getBlockZ());
		config.set(path + "Spawn.World", location1.getWorld().getName());
		config.set(path + "Spawn.2.x", location2.getBlockX());
		config.set(path + "Spawn.2.y", location2.getBlockY());
		config.set(path + "Spawn.2.z", location2.getBlockZ());
		config.set(path + "Spawn.1.Direction", location1.getDirection());
		config.set(path + "Spawn.2.Direction", location2.getDirection());
		config.save();
	}

	@Override
	public void load() {
		this.world = Bukkit.createWorld(new WorldCreator(config.getString(path + "Spawn.World", "PracticeSpawn")));
		this.location1 = new Location(world, config.getInt(path + "Spawn.1.x", 0),
				config.getInt(path + "Spawn.1.y", 70), config.getInt(path + "Spawn.1.z", 0));
		this.location2 = new Location(world, config.getInt(path + "Spawn.2.x", 0),
				config.getInt(path + "Spawn.2.y", 70), config.getInt(path + "Spawn.2.z", 0));
		location1.setDirection(config.getVector(path + "Spawn.1.Direction", null));
		location2.setDirection(config.getVector(path + "Spawn.2.Direction", null));
		this.waitingLocation = new Location(world, config.getInt(path + "Spawn.Waiting.x", 0),
				config.getInt(path + "Spawn.Waiting.y", 70), config.getInt(path + "Spawn.Waiting.z", 0));
		this.displayItem = config.getItemstack(path + "DisplayItem", ItemStacks.DEFAULT_ARENA_DISPLAY_ITEM);
		this.displayName = config.getString(path + "DisplayName", getName());
	}

	@Override
	public void setDefaults() {
		this.world = Bukkit.createWorld(new WorldCreator("PracticeSpawn"));
		this.location1 = new Location(world, 0,
				70, 0);
		this.location2 = new Location(world, 0,
				70, 0);
		this.waitingLocation = new Location(world, 0,
				70, 0);
		this.displayItem = ItemStacks.DEFAULT_ARENA_DISPLAY_ITEM;
		this.displayName = getName();
	}

	@Override
	public void delete() {
		config.remove("Arena." + getName());
		config.save();
	}
}
