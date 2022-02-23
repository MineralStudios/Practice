package gg.mineral.practice.kit;

import org.bukkit.inventory.ItemStack;

public class Kit {
	ItemStack[] contents = new ItemStack[36];
	ItemStack[] armourContents = new ItemStack[4];

	public Kit(ItemStack[] contents, ItemStack[] armourContents) {
		this.contents = contents;
		this.armourContents = armourContents;
	}

	public Kit(Kit kit) {
		this.contents = kit.getContents();
		this.armourContents = kit.getArmourContents();
	}

	public ItemStack[] getContents() {
		return contents;
	}

	public void setContents(ItemStack[] contents) {
		this.contents = contents;
	}

	public ItemStack[] getArmourContents() {
		return armourContents;
	}
}
