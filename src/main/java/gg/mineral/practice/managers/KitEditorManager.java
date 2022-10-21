package gg.mineral.practice.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import gg.mineral.practice.util.FileConfiguration;

public class KitEditorManager {
	final static FileConfiguration config = new FileConfiguration("kiteditor.yml", "plugins/Practice");
	static String displayName;
	static ItemStack displayItem;
	static int slot;
	static Boolean enabled;
	static Location location;

	static {
		load();
	}

	public static void setDisplayName(String displayName) {
		KitEditorManager.displayName = displayName;
		save();
	}

	public static void setDisplayItem(ItemStack displayItem) {
		KitEditorManager.displayItem = displayItem;
		save();
	}

	public static void setSlot(Integer slot) {
		KitEditorManager.slot = slot;
		save();
	}

	public static void setEnabled(boolean enabled) {
		KitEditorManager.enabled = enabled;
		save();
	}

	public static void setLocation(Location location) {
		KitEditorManager.location = location;
		save();
	}

	public static String getDisplayName() {
		return displayName;
	}

	public static ItemStack getDisplayItem() {
		return displayItem;
	}

	public static int getSlot() {
		return slot;
	}

	public static Boolean getEnabled() {
		return enabled;
	}

	public static Location getLocation() {
		return location;
	}

	public static void load() {
		displayName = config.getString("KitEditor.DisplayName", "Kit Editor");
		displayItem = config.getItemstack("KitEditor.DisplayItem", new ItemStack(Material.BOOK));
		slot = config.getInt("KitEditor.Slot", 0);
		enabled = config.getBoolean("KitEditor.Enable", true);
		location = new Location(Bukkit.getWorld(config.getString("KitEditor.Location.World", null)),
				config.getInt("KitEditor.Location.x", 0), config.getInt("KitEditor.Location.y", 70),
				config.getInt("KitEditor.Location.z", 0));
		config.getVector("KitEditor.Location.Direction", null);
	}

	public static void save() {
		config.set("KitEditor.DisplayName", displayName);
		config.set("KitEditor.DisplayItem", displayItem);
		config.set("KitEditor.Slot", slot);
		config.set("KitEditor.Enable", enabled);
		config.set("KitEditor.Location.World", location.getWorld().getName());
		config.set("KitEditor.Location.x", location.getBlockX());
		config.set("KitEditor.Location.y", location.getBlockY());
		config.set("KitEditor.Location.z", location.getBlockZ());
		config.set("KitEditor.Location.Direction", location.getDirection());
		config.save();
	}

	public static void setDefaults() {
		enabled = true;
		displayItem = new ItemStack(Material.BOOK);
		displayName = "Kit Editor";
		slot = 0;
		location = new Location(Bukkit.getWorlds().get(0),
				0, 70, 0);
	}
}
