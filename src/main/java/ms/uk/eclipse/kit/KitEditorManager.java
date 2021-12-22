package ms.uk.eclipse.kit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import land.strafe.api.config.FileConfiguration;

public class KitEditorManager extends FileConfiguration {
	String displayName;
	ItemStack displayItem;
	int slot;
	Boolean enabled;
	Location location;
	Vector direction;

	public KitEditorManager() {
		super("kiteditor.yml", "plugins/Practice");
		displayName = getString("KitEditor.DisplayName", "Kit Editor");
		displayItem = getItemstack("KitEditor.DisplayItem", new ItemStack(Material.BOOK));
		slot = getInt("KitEditor.Slot", 0);
		enabled = getBoolean("KitEditor.Enable", true);
		location = new Location(Bukkit.getWorld(getString("KitEditor.Location.World", null)),
				getInt("KitEditor.Location.x", 0), getInt("KitEditor.Location.y", 70),
				getInt("KitEditor.Location.z", 0));
		getVector("KitEditor.Location.Direction", null);
	}

	public void setDisplayName(String DisplayNameArg) {
		displayName = DisplayNameArg;
		set("KitEditor.DisplayName", DisplayNameArg);
		save();
	}

	public void setDisplayItem(ItemStack DisplayItemArg) {
		displayItem = DisplayItemArg;
		set("KitEditor.DisplayItem", DisplayItemArg);
		save();
	}

	public void setSlot(Integer SlotArg) {
		slot = SlotArg;
		set("KitEditor.Slot", SlotArg);
		save();
	}

	public void setEnabled(boolean EnabledArg) {
		enabled = EnabledArg;
		set("KitEditor.Enable", EnabledArg);
		save();
	}

	public void setLocation(Location loc) {
		location = loc;
		direction = loc.getDirection();
		set("KitEditor.Location.World", loc.getWorld().getName());
		set("KitEditor.Location.x", loc.getBlockX());
		set("KitEditor.Location.y", loc.getBlockY());
		set("KitEditor.Location.z", loc.getBlockZ());
		set("KitEditor.Location.Direction", loc.getDirection());
		save();
	}

	public String getDisplayName() {
		return displayName;
	}

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public int getSlot() {
		return slot;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public Location getLocation() {
		return location;
	}
}
