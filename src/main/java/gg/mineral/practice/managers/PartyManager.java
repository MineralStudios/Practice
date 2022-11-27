package gg.mineral.practice.managers;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.party.Party;
import gg.mineral.practice.util.SaveableData;
import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;

public class PartyManager implements SaveableData {
	GlueList<Party> list = new GlueList<>();
	int slot;
	ItemStack displayItem;
	String displayName;
	Boolean enabled;
	FileConfiguration config = new FileConfiguration("parties.yml", "plugins/Practice");

	public void registerParty(Party party) {
		list.add(party);
	}

	public void remove(Party party) {
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

	public Collection<Party> getPartys() {
		return list;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDisplayItem(ItemStack item) {
		displayItem = item;
	}

	public void setDisplayName(String name) {
		displayName = name;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Integer getSlot() {
		return slot;
	}

	@Override
	public void save() {
		config.set("Parties.Slot", slot);
		config.set("Parties.DisplayName", displayName);
		config.set("Parties.DisplayItem", displayItem);
		config.set("Parties.Enable", enabled);

		config.save();
	}

	@Override
	public void load() {
		slot = config.getInt("Parties.Slot", 4);
		displayItem = config.getItemstack("Parties.DisplayItem", new ItemStack(Material.NETHER_STAR));
		displayName = config.getString("Parties.DisplayName", "Parties");
		enabled = config.getBoolean("Parties.Enable", true);
	}

	@Override
	public void setDefaults() {
		slot = 4;
		displayItem = new ItemStack(Material.NETHER_STAR);
		displayName = "Parties";
		enabled = true;
	}
}
