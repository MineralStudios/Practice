package gg.mineral.practice.catagory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.api.collection.GlueList;
import gg.mineral.api.config.FileConfiguration;
import gg.mineral.practice.gametype.Gametype;
import gg.mineral.practice.managers.CatagoryManager;
import gg.mineral.practice.managers.QueuetypeManager;
import gg.mineral.practice.queue.Queuetype;
import gg.mineral.practice.util.SaveableData;
import lombok.Getter;

public class Catagory implements SaveableData {

	final FileConfiguration config = CatagoryManager.getConfig();

	@Getter
	ItemStack displayItem;
	@Getter
	String displayName;
	@Getter
	final String name;
	@Getter
	GlueList<Gametype> gametypes = new GlueList<>();
	final String path;

	public Catagory(String name) {
		this.name = name;
		this.path = "Catagory." + getName() + ".";
	}

	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
		save();
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		save();
	}

	public void setSlot(Queuetype queuetype, int slot) {
		queuetype.getCatagories().put(this, slot);
		save();
	}

	public void addGametype(Gametype gametype) {
		gametypes.add(gametype);
		save();
	}

	public void removeGametype(Gametype gametype) {
		gametypes.remove(gametype);
		save();
	}

	public void addToQueuetype(Queuetype queuetype, int slot) {
		queuetype.addCatagory(this, slot);
		save();
	}

	public void removeFromQueuetype(Queuetype queuetype) {
		queuetype.getCatagories().remove(this);
		save();
	}

	public boolean equals(Catagory c) {
		return c.getName().equalsIgnoreCase(getName());
	}

	@Override
	public void save() {
		for (Queuetype q : QueuetypeManager.getQueuetypes()) {

			if (!q.getCatagories().containsKey(this)) {
				config.set(path + q.getName() + ".Enabled", false);
				continue;
			}

			int slot = q.getCatagories().getInt(this);

			config.set(path + q.getName() + ".Enabled", true);
			config.set(path + q.getName() + ".Slot", slot);
		}

		config.set(path + "DisplayName", displayName);
		config.set(path + "DisplayItem", displayItem);

		config.save();
	}

	@Override
	public void load() {
		this.displayItem = config.getItemstack(path + "DisplayItem",
				new ItemStack(Material.DIAMOND_SWORD));
		this.displayName = config.getString(path + "DisplayName", getName());

		for (Queuetype q : QueuetypeManager.getQueuetypes()) {
			if (config.getBoolean("Catagory." + getName() + "." + q.getName() + ".Enabled", false)) {
				q.getCatagories().put(this, config.getInt(path + q.getName() + ".Slot", 0));
			}
		}
	}

	@Override
	public void setDefaults() {
		this.displayItem = new ItemStack(Material.DIAMOND_SWORD);
		this.displayName = getName();

		for (Queuetype q : QueuetypeManager.getQueuetypes()) {
			if (config.getBoolean("Catagory." + getName() + "." + q.getName() + ".Enabled", false)) {
				q.getCatagories().put(this, 0);
			}
		}
	}
}
