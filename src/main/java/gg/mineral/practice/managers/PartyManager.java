package gg.mineral.practice.managers;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.party.Party;
import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;

public class PartyManager {
	static GlueList<Party> list = new GlueList<>();
	static int slot;
	static ItemStack displayItem;
	static String displayName;
	static Boolean enabled;
	static FileConfiguration config = new FileConfiguration("parties.yml", "plugins/Practice");

	public static void registerParty(Party party) {
		list.add(party);
	}

	public static void remove(Party party) {
		list.remove(party);
	}

	public boolean contains(Party party) {
		for (Party p : list) {
			if (p.equals(party)) {
				return true;
			}
		}
		return false;
	}

	public Party getParty(UUID u) {
		for (Party p : list) {
			if (p.getPartyLeader().getUUID().equals(u)) {
				return p;
			}
		}

		return null;
	}

	public static Collection<Party> getPartys() {
		return list;
	}

	public static void setEnabled(boolean enabled) {
		PartyManager.enabled = enabled;
	}

	public static void setDisplayItem(ItemStack item) {
		displayItem = item;
	}

	public static void setDisplayName(String name) {
		displayName = name;
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

	public void save() {
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

	public void setDefaults() {
		slot = 4;
		displayItem = new ItemStack(Material.NETHER_STAR);
		displayName = "Parties";
		enabled = true;
	}
}
