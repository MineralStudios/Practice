package gg.mineral.practice.arena;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.util.FileConfiguration;
import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.practice.util.VoidWorldGenerator;
import gg.mineral.practice.util.WorldUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class Arena implements SaveableData {
	public static final List<ChatColor> INCLUDED_COLORS = new GlueList<>(
			Arrays.asList(ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.AQUA,
					ChatColor.WHITE, ChatColor.LIGHT_PURPLE, ChatColor.GRAY));
	Object2ObjectOpenHashMap<ChatColor, Location> bedWarsSpawnLocations = new Object2ObjectOpenHashMap<>();
	boolean bedWarsArena;
	final String name, path;
	Location location1, location2, waitingLocation;
	Vector location1EyeVector, location2EyeVector;
	ItemStack displayItem;
	String displayName;
	int currentNameID = 0;
	World world;

	public Arena(String name) {
		this.name = name;
		this.path = "Arena." + getName() + ".";
	}

	public Location getLocation1() {
		return location1;
	}

	public Location getLocation2() {
		return location2;
	}

	public Vector getLocation1EyeVector() {
		return location1EyeVector;
	}

	public Vector getLocation2EyeVector() {
		return location2EyeVector;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setBedWarsArena(boolean bedWarsArena) {
		this.bedWarsArena = bedWarsArena;
	}

	public boolean isBedWarsArena() {
		return bedWarsArena;
	}

	public void setBedWarsLocation(String color, Location location) {
		for (ChatColor chatColor : INCLUDED_COLORS) {
			if (chatColor.name().contains(color)) {
				bedWarsSpawnLocations.put(chatColor, location);
				return;
			}
		}
	}

	public Location getBedWarsLocation(ChatColor chatColor) {
		return bedWarsSpawnLocations.get(chatColor);
	}

	public String getName() {
		return name;
	}

	public void setLocation1(Location location1) {
		this.location1 = location1;
		save();
	}

	public void setLocation2(Location location2) {
		this.location2 = location2;
		save();
	}

	public void setLocation1EyeVector(Vector location1EyeVector) {
		this.location1EyeVector = location1EyeVector;
		save();
	}

	public void setLocation2EyeVector(Vector location2EyeVector) {
		this.location2EyeVector = location2EyeVector;
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

	public Location getWaitingLocation() {
		return this.waitingLocation;
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
		FileConfiguration config = ArenaManager.getConfig();
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
		config.set(path + "Spawn.1.Direction", location1EyeVector);
		config.set(path + "Spawn.2.Direction", location2EyeVector);
		config.save();
	}

	@Override
	public void load() {
		FileConfiguration config = ArenaManager.getConfig();
		this.world = Bukkit.createWorld(new WorldCreator(config.getString(path + "Spawn.World", "PracticeSpawn")));
		this.location1 = new Location(world, config.getInt(path + "Spawn.1.x", 0),
				config.getInt(path + "Spawn.1.y", 70), config.getInt(path + "Spawn.1.z", 0));
		this.location2 = new Location(world, config.getInt(path + "Spawn.2.x", 0),
				config.getInt(path + "Spawn.2.y", 70), config.getInt(path + "Spawn.2.z", 0));
		this.waitingLocation = new Location(world, config.getInt(path + "Spawn.Waiting.x", 0),
				config.getInt(path + "Spawn.Waiting.y", 70), config.getInt(path + "Spawn.Waiting.z", 0));
		this.location1EyeVector = config.getVector(path + "Spawn.1.Direction", null);
		this.location2EyeVector = config.getVector(path + "Spawn.2.Direction", null);
		this.displayItem = config.getItemstack(path + "DisplayItem", new ItemStack(Material.WOOL));
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
		this.location1EyeVector = location1.getDirection();
		this.location2EyeVector = location2.getDirection();
		this.displayItem = new ItemStack(Material.WOOL);
		this.displayName = getName();
	}
}
