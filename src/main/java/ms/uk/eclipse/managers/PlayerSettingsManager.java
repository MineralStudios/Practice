package ms.uk.eclipse.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import land.strafe.api.config.FileConfiguration;
import ms.uk.eclipse.util.SaveableData;

public class PlayerSettingsManager implements SaveableData {
	final FileConfiguration config = new FileConfiguration("playeroptions.yml", "plugins/Practice");
	int slot;
	ItemStack displayItem;
	String displayName;
	Boolean enabled;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDisplayItem(ItemStack display) {
		displayItem = display;
	}

	public void setDisplayName(String name) {
		displayName = name;
	}

	public void setSlot(Integer slot) {
		this.slot = slot;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getSlot() {
		return slot;
	}

	@Override
	public void save() {
		config.set("Options.Enable", enabled);
		config.set("Options.DisplayItem", displayItem);
		config.set("Options.DisplayName", displayName);
		config.set("Options.Slot", slot);
		config.save();
	}

	@Override
	public void load() {
		slot = config.getInt("Options.Slot", 3);
		displayItem = config.getItemstack("Options.DisplayItem", new ItemStack(Material.COMPASS));
		displayName = config.getString("Options.DisplayName", "Settings");
		enabled = config.getBoolean("Options.Enable", true);
	}

	@Override
	public void setDefaults() {
		slot = 3;
		displayItem = new ItemStack(Material.COMPASS);
		displayName = "Settings";
		enabled = true;
	}
}
