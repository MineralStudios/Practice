package gg.mineral.practice.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.FileConfiguration;

public class PvPBotsManager {
	final static FileConfiguration config = new FileConfiguration("pvpbots.yml", "plugins/Practice");
	static boolean enabled;
	static ItemStack displayItem;
	static String displayName;
	static public int slot;

	static {
		load();
	}

	public static void setEnabled(boolean enabled) {
		PvPBotsManager.enabled = enabled;
	}

	public static void setDisplayItem(ItemStack displayItem) {
		PvPBotsManager.displayItem = displayItem;
	}

	public static void setDisplayName(String displayName) {
		PvPBotsManager.displayName = displayName;
	}

	public static void setSlot(int slot) {
		PvPBotsManager.slot = slot;
	}

	public static void load() {
		enabled = config.getBoolean("Bot.Enable", true);
		displayItem = config.getItemstack("Bot.DisplayItem", new ItemStack(Material.BLAZE_ROD));
		displayName = config.getString("Bot.DisplayName", "PvP Bots");
		slot = config.getInt("Bot.Slot", 5);
	}

	public static void save() {
		config.set("Bot.Enable", enabled);
		config.set("Bot.DisplayItem", displayItem);
		config.set("Bot.DisplayName", displayName);
		config.set("Bot.Slot", slot);
		config.save();
	}

	public static void setDefaults() {
		enabled = false;
		displayItem = new ItemStack(Material.BLAZE_ROD);
		displayName = "PvP Bots";
		slot = 5;
	}
}
