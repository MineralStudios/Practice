package gg.mineral.practice.managers;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.util.items.ItemStacks;

public class LeaderboardManager {
    final static FileConfiguration config = new FileConfiguration("leaderboardoptions.yml", "plugins/Practice");
    static int slot;
    static ItemStack displayItem;
    static String displayName;
    static Boolean enabled;

    public static void setEnabled(boolean enabled) {
        LeaderboardManager.enabled = enabled;
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
        LeaderboardManager.slot = slot;
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
        config.set("Leaderboard.Enable", enabled);
        config.set("Leaderboard.DisplayItem", displayItem);
        config.set("Leaderboard.DisplayName", displayName);
        config.set("Leaderboard.Slot", slot);
        config.save();
    }

    public static void load() {
        slot = config.getInt("Leaderboard.Slot", 3);
        displayItem = config.getItemstack("Leaderboard.DisplayItem", ItemStacks.DEFAULT_LEADERBOARD_DISPLAY_ITEM);
        displayName = config.getString("Leaderboard.DisplayName", "Leaderboard");
        enabled = config.getBoolean("Leaderboard.Enable", true);
    }

    public void setDefaults() {
        slot = 3;
        displayItem = ItemStacks.DEFAULT_LEADERBOARD_DISPLAY_ITEM;
        displayName = "Leaderboard";
        enabled = true;
    }
}
