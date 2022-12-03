package gg.mineral.practice.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.config.FileConfiguration;

public class PlayerSettingsManager {
	final static FileConfiguration config = new FileConfiguration("playeroptions.yml", "plugins/Practice");
	static int slot;
	static ItemStack displayItem;
	static String displayName;
	static Boolean enabled;

	public static void setEnabled(boolean enabled) {
		PlayerSettingsManager.enabled = enabled;
		save();
	}

	public static void setDisplayItem(ItemStack display) {
		displayItem = display;
		save();
	}

	public static void setDisplayName(String name) {
		displayName = name;
		save();
	}

	public static void setSlot(Integer slot) {
		PlayerSettingsManager.slot = slot;
		save();
	}

	public static Boolean getEnabled() {
		return enabled;
	}

	public static ItemStack getDisplayItem() {
		return displayItem;
	}

	public static String getDisplayName() {
		return displayName;
	}

	public static int getSlot() {
		return slot;
	}

	public static void save() {
		config.set("Options.Enable", enabled);
		config.set("Options.DisplayItem", displayItem);
		config.set("Options.DisplayName", displayName);
		config.set("Options.Slot", slot);
		config.save();
	}

	public static void load() {
		slot = config.getInt("Options.Slot", 3);
		displayItem = config.getItemstack("Options.DisplayItem", new ItemStack(Material.COMPASS));
		displayName = config.getString("Options.DisplayName", "Settings");
		enabled = config.getBoolean("Options.Enable", true);
	}

	public void setDefaults() {
		slot = 3;
		displayItem = new ItemStack(Material.COMPASS);
		displayName = "Settings";
		enabled = true;
	}
}
