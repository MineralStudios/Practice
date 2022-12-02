package gg.mineral.practice.managers;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import gg.mineral.practice.util.messages.Message;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.util.collection.ProfileList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import gg.mineral.api.config.FileConfiguration;

public class PlayerManager {
	final static FileConfiguration lobbyConfig = new FileConfiguration("lobby.yml", "plugins/Practice");
	final static FileConfiguration playerConfig = new FileConfiguration("PlayerData.yml",
			"plugins/Practice/PlayerData");
	static Location spawnLocation;
	static ProfileList profileList = new ProfileList();
	static ProfileList profilesInMatch = new ProfileList();
	static Object2ObjectOpenHashMap<String, InventoryStatsMenu> inventoryStats = new Object2ObjectOpenHashMap<>();
	static Object2ObjectOpenHashMap<String, InventoryStatsMenu> partyInventoryStats = new Object2ObjectOpenHashMap<>();

	public static void add(Profile player) {
		profileList.add(player);
	}

	public static void remove(Profile player) {
		profileList.remove(player);
		profilesInMatch.remove(player);
	}

	public static ProfileList getProfiles() {
		return profileList;
	}

	public static int getOfflinePlayerElo(Gametype g, String name) {
		return EloManager.getEloEntry(name, g.getName());
	}

	public static Profile getProfile(String string) {
		for (int i = 0; i < profileList.size(); i++) {
			Profile p = profileList.get(i);
			if (p.getName().equalsIgnoreCase(string)) {
				return p;
			}
		}

		return null;
	}

	public static InventoryStatsMenu getInventoryStats(String s) {
		return inventoryStats.get(s);
	}

	public static Profile getProfile(UUID u) {
		for (int i = 0; i < profileList.size(); i++) {
			Profile p = profileList.get(i);
			if (p.getUUID().equals(u)) {
				return p;
			}
		}
		return null;
	}

	public static Profile getProfile(org.bukkit.entity.Player pl) {
		if (pl == null) {
			return null;
		}

		Profile val = getProfile(pl.getUniqueId());

		if (val == null) {
			val = new Profile(pl);
			add(val);
		}

		return val;
	}

	public static Profile getProfileFromMatch(UUID u) {
		for (int i = 0; i < profilesInMatch.size(); i++) {
			Profile p = profilesInMatch.get(i);
			if (p.getUUID().equals(u)) {
				return p;
			}
		}

		return null;
	}

	public static Profile getProfileFromMatch(String s) {
		for (int i = 0; i < profilesInMatch.size(); i++) {
			Profile p = profilesInMatch.get(i);
			if (p.getName().equalsIgnoreCase(s)) {
				return p;
			}
		}
		return null;
	}

	public static Profile getProfileFromMatch(org.bukkit.entity.Player pl) {
		return getProfileFromMatch(pl.getUniqueId());
	}

	public boolean contains(Profile pl) {
		return profileList.contains(pl);
	}

	public static void setSpawnLocation(Location loc) {
		spawnLocation = loc;
	}

	public static FileConfiguration getConfig() {
		return playerConfig;
	}

	public static Location getSpawnLocation() {
		return spawnLocation;
	}

	public static void setInMatch(Profile p) {
		profilesInMatch.add(p);
	}

	public static void removeFromMatch(Profile p) {
		profilesInMatch.remove(p);
	}

	public static ProfileList getProfilesInMatch() {
		return profilesInMatch;
	}

	public static void setInventoryStats(Profile p, InventoryStatsMenu menu) {
		inventoryStats.put(p.getName(), menu);
	}

	public static void setPartyInventoryStats(Profile p, InventoryStatsMenu menu) {
		partyInventoryStats.put(p.getName(), menu);
	}

	public static void broadcast(Collection<Profile> c, Message message) {
		c.parallelStream().forEach(p -> p.message(message));
	}

	public void save() {
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

	public void setDefaults() {
		spawnLocation = new Location(Bukkit.getWorlds().get(0),
				0, 70, 0);
	}
}
