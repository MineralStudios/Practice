package gg.mineral.practice.managers;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.FileConfiguration;
import gg.mineral.practice.util.messages.Message;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.util.ProfileList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class PlayerManager {
	final static FileConfiguration lobbyConfig = new FileConfiguration("lobby.yml", "plugins/Practice"),
			playerConfig = new FileConfiguration("PlayerData.yml", "plugins/Practice/PlayerData");
	static Location spawnLocation;
	static ProfileList profileList = new ProfileList();
	static Object2ObjectOpenHashMap<String, InventoryStatsMenu> inventoryStats = new Object2ObjectOpenHashMap<>(),
			partyInventoryStats = new Object2ObjectOpenHashMap<>();

	static {
		load();
	}

	public static void register(Profile player) {
		profileList.add(player);
	}

	public static void remove(Profile player) {
		profileList.remove(player);
	}

	public static ProfileList list() {
		return profileList;
	}

	public static int getOfflinePlayerElo(String name, Gametype gametype) throws SQLException {
		return EloManager.getByName(name, gametype);
	}

	public static InventoryStatsMenu getInventoryStats(String name) {
		return inventoryStats.get(name);
	}

	public static Profile get(Predicate<Profile> predicate) {
		for (Profile profile : list()) {
			if (!predicate.test(profile)) {
				continue;
			}

			return profile;
		}

		return null;
	}

	public static List<Profile> getList(Predicate<Profile> predicate) {
		GlueList<Profile> profileList = new GlueList<>();

		for (Profile profile : list()) {
			if (!predicate.test(profile)) {
				continue;
			}

			profileList.add(profile);
		}

		return profileList;
	}

	public static Profile getOrCreate(Player bukkitPlayer) {
		Profile val = get(profile -> profile.getUUID().equals(bukkitPlayer.getUniqueId()));

		if (val == null) {
			register(val = new Profile(bukkitPlayer));
		}

		return val;
	}

	public static void setSpawnLocation(Location spawnLocation) {
		PlayerManager.spawnLocation = spawnLocation;
	}

	public static FileConfiguration getConfig() {
		return playerConfig;
	}

	public static Location getSpawnLocation() {
		return spawnLocation;
	}

	public static void setInventoryStats(Profile p, InventoryStatsMenu menu) {
		inventoryStats.put(p.getName(), menu);
	}

	public static void setPartyInventoryStats(Profile p, InventoryStatsMenu menu) {
		partyInventoryStats.put(p.getName(), menu);
	}

	public static void broadcast(Collection<Profile> profiles, Message message) {
		profiles.forEach(profile -> profile.message(message));
	}

	public static void save() {
		lobbyConfig.set("Lobby.World", spawnLocation.getWorld().getName());
		lobbyConfig.set("Lobby.x", spawnLocation.getBlockX());
		lobbyConfig.set("Lobby.y", spawnLocation.getBlockY());
		lobbyConfig.set("Lobby.z", spawnLocation.getBlockZ());
		lobbyConfig.set("Lobby.Direction", spawnLocation.getDirection());
		lobbyConfig.save();
	}

	public static void load() {
		Vector spawnDirection = lobbyConfig.getVector("Lobby.Direction", new Vector());
		spawnLocation = new Location(
				Bukkit.getWorld(lobbyConfig.getString("Lobby.World", Bukkit.getWorlds().get(0).getName())),
				lobbyConfig.getInt("Lobby.x", 0), lobbyConfig.getInt("Lobby.y", 70), lobbyConfig.getInt("Lobby.z", 0));
		spawnLocation.setDirection(spawnDirection);
	}

	public static void setDefaults() {
		spawnLocation = new Location(Bukkit.getWorlds().get(0),
				0, 70, 0);
	}
}
