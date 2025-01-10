package gg.mineral.practice.managers;

import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.util.items.ItemStacks;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class PartyManager {
    @Getter
    private static Map<UUID, Party> parties = new Object2ObjectOpenHashMap<>();
    @Getter
    static int slot;
    @Getter
    static ItemStack displayItem;
    @Getter
    static String displayName;
    @Getter
    static boolean enabled;
    static FileConfiguration config = new FileConfiguration("parties.yml", "plugins/Practice");

    public static void registerParty(Party party) {
        parties.put(party.getUuid(), party);
    }

    public static void remove(Party party) {
        parties.remove(party.getUuid());
    }

    public boolean contains(Party party) {
        return parties.containsKey(party.getUuid());
    }

    public Party getParty(UUID uuid) {
        return parties.get(uuid);
    }

    public static void setEnabled(boolean enabled) {
        PartyManager.enabled = enabled;
        save();
    }

    public static void setDisplayItem(ItemStack item) {
        displayItem = item;
        save();
    }

    public static void setDisplayName(String name) {
        displayName = name;
        save();
    }

    public static void setSlot(int slot) {
        PartyManager.slot = slot;
        save();
    }

    public static void save() {
        config.set("Parties.Slot", slot);
        config.set("Parties.DisplayName", displayName);
        config.set("Parties.DisplayItem", displayItem);
        config.set("Parties.Enable", enabled);

        config.save();
    }

    public static void load() {
        slot = config.getInt("Parties.Slot", 4);
        displayItem = config.getItemstack("Parties.DisplayItem", ItemStacks.DEFAULT_PARTY_DISPLAY_ITEM);
        displayName = config.getString("Parties.DisplayName", "Parties");
        enabled = config.getBoolean("Parties.Enable", true);
    }

    public static void setDefaults() {
        slot = 4;
        displayItem = ItemStacks.DEFAULT_PARTY_DISPLAY_ITEM;
        displayName = "Parties";
        enabled = true;
    }
}
