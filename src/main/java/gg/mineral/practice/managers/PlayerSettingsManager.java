package gg.mineral.practice.managers;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.util.items.ItemStacks;
import lombok.Getter;

public class PlayerSettingsManager {
	final static FileConfiguration config = new FileConfiguration("playeroptions.yml", "plugins/Practice");
	@Getter
	static int slot;
	@Getter
	static ItemStack displayItem;
	@Getter
	static String displayName;
	@Getter
	static boolean enabled;

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

	public static void setSlot(int slot) {
		PlayerSettingsManager.slot = slot;
		save();
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
		displayItem = config.getItemstack("Options.DisplayItem", ItemStacks.DEFAULT_OPTIONS_DISPLAY_ITEM);
		displayName = config.getString("Options.DisplayName", "Settings");
		enabled = config.getBoolean("Options.Enable", true);
	}

	public void setDefaults() {
		slot = 3;
		displayItem = ItemStacks.DEFAULT_OPTIONS_DISPLAY_ITEM;
		displayName = "Settings";
		enabled = true;
	}
}
