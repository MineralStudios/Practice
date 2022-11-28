package gg.mineral.practice.managers;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import gg.mineral.practice.util.messages.Message;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.inventory.menus.InventoryStatsMenu;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.SaveableData;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import gg.mineral.api.config.FileConfiguration;

public class PlayerManager implements SaveableData {
	final FileConfiguration lobbyConfig = new FileConfiguration("lobby.yml", "plugins/Practice");
	final FileConfiguration playerConfig = new FileConfiguration("PlayerData.yml", "plugins/Practice/PlayerData");
	Location spawnLocation;
	ProfileList profileList = new ProfileList();
	ProfileList profilesInMatch = new ProfileList();
	Object2ObjectOpenHashMap<String, InventoryStatsMenu> inventoryStats = new Object2ObjectOpenHashMap<>();
	Object2ObjectOpenHashMap<String, InventoryStatsMenu> partyInventoryStats = new Object2ObjectOpenHashMap<>();
	final EloManager eloManager = PracticePlugin.INSTANCE.getEloManager();

	public void add(Profile player) {
		profileList.add(player);
	}

	public void remove(Profile player) {
		profileList.remove(player);
		profilesInMatch.remove(player);
	}

	public ProfileList getProfiles() {
		return profileList;
	}

	public int getOfflinePlayerElo(Gametype g, String name) {
		return eloManager.getEloEntry(name, g.getName());
	}

	public Profile getProfile(String string) {
		for (int i = 0; i < profileList.size(); i++) {
			Profile p = profileList.get(i);
			if (p.getName().equalsIgnoreCase(string)) {
				return p;
			}
		}

		return null;
	}

	public InventoryStatsMenu getInventoryStats(String s) {
		return inventoryStats.get(s);
	}

	public Profile getProfile(UUID u) {
		for (int i = 0; i < profileList.size(); i++) {
			Profile p = profileList.get(i);
			if (p.getUUID().equals(u)) {
				return p;
			}
		}
		return null;
	}

	public Profile getProfile(org.bukkit.entity.Player pl) {
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

	public Profile getProfileFromMatch(UUID u) {
		for (int i = 0; i < profilesInMatch.size(); i++) {
			Profile p = profilesInMatch.get(i);
			if (p.getUUID().equals(u)) {
				return p;
			}
		}

		return null;
	}

	public Profile getProfileFromMatch(String s) {
		for (int i = 0; i < profilesInMatch.size(); i++) {
			Profile p = profilesInMatch.get(i);
			if (p.getName().equalsIgnoreCase(s)) {
				return p;
			}
		}
		return null;
	}

	public Profile getProfileFromMatch(org.bukkit.entity.Player pl) {
		return getProfileFromMatch(pl.getUniqueId());
	}

	public boolean contains(Profile pl) {
		return profileList.contains(pl);
	}

	public void setSpawnLocation(Location loc) {
		spawnLocation = loc;
	}

	public FileConfiguration getConfig() {
		return playerConfig;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void setInMatch(Profile p) {
		profilesInMatch.add(p);
	}

	public void removeFromMatch(Profile p) {
		profilesInMatch.remove(p);
	}

	public ProfileList getProfilesInMatch() {
		return profilesInMatch;
	}

	public void setInventoryStats(Profile p, InventoryStatsMenu menu) {
		inventoryStats.put(p.getName(), menu);
	}

	public void setPartyInventoryStats(Profile p, InventoryStatsMenu menu) {
		partyInventoryStats.put(p.getName(), menu);
	}

	public void broadcast(Collection<Profile> c, Message message) {
		c.parallelStream().forEach(p -> p.message(message));
	}

	@Override
	public void save() {
		lobbyConfig.set("Lobby.World", spawnLocation.getWorld().getName());
		lobbyConfig.set("Lobby.x", spawnLocation.getBlockX());
		lobbyConfig.set("Lobby.y", spawnLocation.getBlockY());
		lobbyConfig.set("Lobby.z", spawnLocation.getBlockZ());
		lobbyConfig.set("Lobby.Direction", spawnLocation.getDirection());
		lobbyConfig.save();
	}

	@Override
	public void load() {
		Vector spawnDirection = lobbyConfig.getVector("Lobby.Direction", new Vector());
		spawnLocation = new Location(
				Bukkit.getWorld(lobbyConfig.getString("Lobby.World", Bukkit.getWorlds().get(0).getName())),
				lobbyConfig.getInt("Lobby.x", 0), lobbyConfig.getInt("Lobby.y", 70), lobbyConfig.getInt("Lobby.z", 0));
		spawnLocation.setDirection(spawnDirection);
	}

	@Override
	public void setDefaults() {
		spawnLocation = new Location(Bukkit.getWorlds().get(0),
				0, 70, 0);
	}
}
