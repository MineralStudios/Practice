package ms.uk.eclipse.managers;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import land.strafe.api.config.FileConfiguration;
import ms.uk.eclipse.util.SaveableData;

public class PvPBotsManager implements SaveableData {
	final FileConfiguration config = new FileConfiguration("pvpbots.yml", "plugins/Practice");
	public boolean enabled;
	public ItemStack displayItem;
	public String displayName;
	public int slot;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setDisplayItem(ItemStack i) {
		displayItem = i;
	}

	public void setDisplayName(String name) {
		displayName = name;
	}

	public void setSlot(int s) {
		slot = s;
	}

	@Override
	public void load() {
		enabled = config.getBoolean("Bot.Enable", true);
		displayItem = config.getItemstack("Bot.DisplayItem", new ItemStack(Material.BLAZE_ROD));
		displayName = config.getString("Bot.DisplayName", "PvP Bots");
		slot = config.getInt("Bot.Slot", 5);
	}

	@Override
	public void save() {
		config.set("Bot.Enable", enabled);
		config.set("Bot.DisplayItem", displayItem);
		config.set("Bot.DisplayName", displayName);
		config.set("Bot.Slot", slot);
		config.save();
	}
}
