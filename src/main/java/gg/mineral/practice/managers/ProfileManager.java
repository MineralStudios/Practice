package gg.mineral.practice.managers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.bot.api.BotAPI;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.entity.ProfileData;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.util.messages.Message;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;

public class ProfileManager {
	final static FileConfiguration lobbyConfig = new FileConfiguration("lobby.yml", "plugins/Practice");
	@Getter
	final static FileConfiguration playerConfig = new FileConfiguration("PlayerData.yml",
			"plugins/Practice/PlayerData");
	@Getter
	static Location spawnLocation;

	@Getter
	public

	static class ProfileMap extends Object2ObjectOpenHashMap<UUID, Profile> {
		@Getter
		private int playerCount = 0, botCount = 0;

		@Override
		public Profile computeIfAbsent(UUID key,
				Object2ObjectFunction<? super UUID, ? extends Profile> mappingFunction) {
			int oldSize = size();
			val profile = super.computeIfAbsent(key, mappingFunction);
			int newSize = size();

			if (oldSize < newSize) {
				if (BotAPI.INSTANCE.isFakePlayer(key))
					botCount++;
				else
					playerCount++;
			} else if (oldSize > newSize) {
				if (BotAPI.INSTANCE.isFakePlayer(key))
					botCount--;
				else
					playerCount--;
			}

			return profile;
		}

		@Override
		public Profile put(UUID key, Profile value) {
			int oldSize = size();
			val profile = super.put(key, value);
			int newSize = size();

			if (oldSize < newSize) {
				if (BotAPI.INSTANCE.isFakePlayer(key))
					botCount++;
				else
					playerCount++;
			} else if (oldSize > newSize) {
				if (BotAPI.INSTANCE.isFakePlayer(key))
					botCount--;
				else
					playerCount--;
			}

			return profile;
		}

		@Override
		public Profile remove(Object key) {
			int oldSize = size();
			val profile = super.remove(key);
			int newSize = size();

			if (key instanceof UUID uuid) {
				if (oldSize < newSize) {
					if (BotAPI.INSTANCE.isFakePlayer(uuid))
						botCount++;
					else
						playerCount++;
				} else if (oldSize > newSize) {
					if (BotAPI.INSTANCE.isFakePlayer(uuid))
						botCount--;
					else
						playerCount--;
				}
			}

			return profile;
		}
	}

	@Getter
	private static ProfileMap profiles = new ProfileMap();
	private static Object2ObjectOpenHashMap<String, InventoryStatsMenu> inventoryStats = new Object2ObjectOpenHashMap<>();
	private static Object2ObjectOpenHashMap<String, List<InventoryStatsMenu>> teamInventoryStats = new Object2ObjectOpenHashMap<>();

	public static void add(Profile profile) {
		profiles.put(profile.getUuid(), profile);
	}

	public static void remove(Profile profile) {
		profiles.remove(profile.getUuid());
	}

	public static InventoryStatsMenu getInventoryStats(String s) {
		return inventoryStats.get(s);
	}

	public static List<InventoryStatsMenu> getTeamInventoryStats(String s) {
		return teamInventoryStats.get(s);
	}

	public static int count(Predicate<Profile> predicate) {
		int count = 0;
		for (val profile : profiles.values()) {
			if (!predicate.test(profile))
				continue;

			count++;
		}

		return count;
	}

	public static int countBots() {
		return profiles.botCount;
	}

	public static ProfileData getProfileData(String name, UUID uuid) {

		if (uuid != null) {
			val profile = profiles.get(uuid);

			if (profile != null)
				return profile;
		}

		for (val p : profiles.values()) {
			if (!p.getName().equalsIgnoreCase(name))
				continue;

			return p;
		}

		return new ProfileData(uuid, name);
	}

	@Nullable
	public static Profile getProfile(UUID uuid) {
		return profiles.get(uuid);
	}

	@Nullable
	public static Profile getProfile(UUID uuid, Predicate<Profile> predicate) {
		val profile = profiles.get(uuid);

		if (profile == null || !predicate.test(profile))
			return null;

		return profile;
	}

	@Nullable
	public static Profile getProfile(String name) {
		for (val p : profiles.values()) {
			if (!p.getName().equalsIgnoreCase(name))
				continue;

			return p;
		}
		return null;
	}

	@Nullable
	public static Profile getProfile(String name, Predicate<Profile> predicate) {
		val profile = getProfile(name);

		if (profile == null || !predicate.test(profile))
			return null;

		return profile;
	}

	public static void removeIfExists(org.bukkit.entity.Player pl) {
		if (pl == null)
			return;

		profiles.remove(pl.getUniqueId());
	}

	public static Profile getOrCreateProfile(org.bukkit.entity.Player pl) {
		if (pl == null)
			return null;

		return profiles.computeIfAbsent(pl.getUniqueId(), k -> new Profile(pl));
	}

	public static void setInventoryStats(Profile p, InventoryStatsMenu menu) {
		inventoryStats.put(p.getName(), menu);
	}

	public static void setTeamInventoryStats(Profile p, List<InventoryStatsMenu> menus) {
		teamInventoryStats.put(p.getName(), menus);
	}

	public static void broadcast(Collection<Profile> c, Message message) {
		for (Profile p : c)
			p.message(message);
	}

	public static void broadcast(Message message) {
		broadcast(profiles.values(), message);
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
		val world = Bukkit.createWorld(
				new WorldCreator(lobbyConfig.getString("Lobby.World", Bukkit.getWorlds().get(0).getName())));
		val spawnDirection = lobbyConfig.getVector("Lobby.Direction", new Vector());
		spawnLocation = new Location(
				world,
				lobbyConfig.getInt("Lobby.x", 0), lobbyConfig.getInt("Lobby.y", 70), lobbyConfig.getInt("Lobby.z", 0));
		spawnLocation.setDirection(spawnDirection);
	}

	public void setDefaults() {
		spawnLocation = new Location(Bukkit.getWorlds().get(0),
				0, 70, 0);
	}

	public static void setSpawnLocation(Location loc) {
		spawnLocation = loc;
		save();
	}

	public static Profile getProfile(Player player) {
		return profiles.get(player.getUniqueId());
	}
}
