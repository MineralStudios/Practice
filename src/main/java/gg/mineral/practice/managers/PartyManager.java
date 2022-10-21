package gg.mineral.practice.managers;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.GlueList;
import gg.mineral.practice.util.FileConfiguration;
import gg.mineral.practice.party.Party;

public class PartyManager {
	final static GlueList<Party> list = new GlueList<>();
	static int slot;
	static ItemStack displayItem;
	static String displayName;
	static Boolean enabled;
	final static FileConfiguration config = new FileConfiguration("parties.yml", "plugins/Practice");

	static {
		load();
	}

	public static void register(Party party) {
		list.add(party);
	}

	public static void remove(Party party) {
		list.remove(party);
	}

	public static Party get(UUID uuid) {
		for (Party party : list()) {
			if (!party.getPartyLeader().getUUID().equals(uuid)) {
				continue;
			}

			return party;
		}

		return null;
	}

	public static List<Party> list() {
		return list;
	}

	public static void setEnabled(boolean enabled) {
		PartyManager.enabled = enabled;
	}

	public static void setDisplayItem(ItemStack displayItem) {
		PartyManager.displayItem = displayItem;
	}

	public static void setDisplayName(String displayName) {
		PartyManager.displayName = displayName;
	}

	public static void setSlot(Integer slot) {
		PartyManager.slot = slot;
	}

	public static boolean getEnabled() {
		return enabled;
	}

	public static ItemStack getDisplayItem() {
		return displayItem;
	}

	public static String getDisplayName() {
		return displayName;
	}

	public static Integer getSlot() {
		return slot;
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
		displayItem = config.getItemstack("Parties.DisplayItem", new ItemStack(Material.NETHER_STAR));
		displayName = config.getString("Parties.DisplayName", "Parties");
		enabled = config.getBoolean("Parties.Enable", true);
	}

	public static void setDefaults() {
		slot = 4;
		displayItem = new ItemStack(Material.NETHER_STAR);
		displayName = "Parties";
		enabled = true;
	}
}
