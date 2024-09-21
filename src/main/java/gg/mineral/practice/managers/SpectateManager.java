package gg.mineral.practice.managers;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.util.items.ItemStacks;
import lombok.Getter;

public class SpectateManager {
    final static FileConfiguration config = new FileConfiguration("spectateoptions.yml", "plugins/Practice");
    @Getter
    static int slot;
    @Getter
    static ItemStack displayItem;
    @Getter
    static String displayName;
    @Getter
    static boolean enabled;

    public static void setEnabled(boolean enabled) {
        SpectateManager.enabled = enabled;
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
        SpectateManager.slot = slot;
        save();
    }

    public static void save() {
        config.set("Spectate.Enable", enabled);
        config.set("Spectate.DisplayItem", displayItem);
        config.set("Spectate.DisplayName", displayName);
        config.set("Spectate.Slot", slot);
        config.save();
    }

    public static void load() {
        slot = config.getInt("Spectate.Slot", 3);
        displayItem = config.getItemstack("Spectate.DisplayItem", ItemStacks.DEFAULT_SPECTATE_DISPLAY_ITEM);
        displayName = config.getString("Spectate.DisplayName", "Spectate");
        enabled = config.getBoolean("Spectate.Enable", true);
    }

    public void setDefaults() {
        slot = 3;
        displayItem = ItemStacks.DEFAULT_OPTIONS_DISPLAY_ITEM;
        displayName = "Spectate";
        enabled = true;
    }
}
