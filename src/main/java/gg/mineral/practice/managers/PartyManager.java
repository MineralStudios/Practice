package gg.mineral.practice.managers;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.party.Party;
import gg.mineral.practice.util.items.ItemStacks;
import lombok.Getter;

public class PartyManager {
	@Getter
	static GlueList<Party> parties = new GlueList<>();
	@Getter
	static int slot;
	@Getter
	static ItemStack displayItem;
	@Getter
	static String displayName;
	@Getter
	static Boolean enabled;
	static FileConfiguration config = new FileConfiguration("parties.yml", "plugins/Practice");

	public static void registerParty(Party party) {
		parties.add(party);
	}

	public static void remove(Party party) {
		parties.remove(party);
	}

	public boolean contains(Party party) {
		for (Party p : parties) {
			if (p.equals(party)) {
				return true;
			}
		}
		return false;
	}

	public Party getParty(UUID u) {
		for (Party p : parties) {
			if (p.getPartyLeader().getUUID().equals(u)) {
				return p;
			}
		}

		return null;
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

	public static void setSlot(Integer slot) {
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
