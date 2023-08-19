package gg.mineral.practice.managers;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.Message;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class ProfileManager {
	final static FileConfiguration lobbyConfig = new FileConfiguration("lobby.yml", "plugins/Practice");
	@Getter
	final static FileConfiguration playerConfig = new FileConfiguration("PlayerData.yml",
			"plugins/Practice/PlayerData");
	@Setter
	@Getter
	static Location spawnLocation;
	@Getter
	static ProfileList profiles = new ProfileList();
	static Object2ObjectOpenHashMap<String, InventoryStatsMenu> inventoryStats = new Object2ObjectOpenHashMap<>();
	static Object2ObjectOpenHashMap<String, List<InventoryStatsMenu>> teamInventoryStats = new Object2ObjectOpenHashMap<>();

	public static void add(Profile profile) {
		profiles.add(profile);
	}

	public static void remove(Profile profile) {
		profiles.remove(profile);
	}

	public static CompletableFuture<Integer> getOfflinePlayerElo(Gametype g, String name) {
		return EloManager.get(g, name);
	}

	public static InventoryStatsMenu getInventoryStats(String s) {
		return inventoryStats.get(s);
	}

	public static List<InventoryStatsMenu> getTeamInventoryStats(String s) {
		return teamInventoryStats.get(s);
	}

	public static int count(Predicate<Profile> predicate) {
		int count = 0;
		for (Profile profile : profiles) {

			if (!predicate.test(profile)) {
				continue;
			}

			count++;
		}

		return count;
	}

	public static Profile getProfile(Predicate<Profile> predicate) {
		for (Profile profile : profiles) {

			if (!predicate.test(profile)) {
				continue;
			}

			return profile;
		}

		return null;
	}

	public static void removeIfExists(org.bukkit.entity.Player pl) {
		if (pl == null)
			return;

		Profile profile = getProfile(p -> p.getUUID().equals(pl.getUniqueId()));

		remove(profile);
	}

	public static Profile getOrCreateProfile(org.bukkit.entity.Player pl) {
		if (pl == null) {
			return null;
		}

		Profile profile = getProfile(p -> p.getUUID().equals(pl.getUniqueId()));

		if (profile == null) {
			add(profile = new Profile(pl));
		}

		return profile;
	}

	public static void setInventoryStats(Profile p, InventoryStatsMenu menu) {
		inventoryStats.put(p.getName(), menu);
	}

	public static void setTeamInventoryStats(Profile p, List<InventoryStatsMenu> menus) {
		teamInventoryStats.put(p.getName(), menus);
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
