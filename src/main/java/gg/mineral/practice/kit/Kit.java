package gg.mineral.practice.kit;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class Kit {
	@Getter
	@Setter
	ItemStack[] contents = new ItemStack[36], armourContents = new ItemStack[4];
	@Getter
	String name;

	public Kit(String name, ItemStack[] contents, ItemStack[] armourContents) {
		this.contents = contents;
		this.armourContents = armourContents;
	}

	public Kit(ItemStack[] contents, ItemStack[] armourContents) {
		this("Custom", contents, armourContents);
	}

	public Kit(Kit kit) {
		this.name = kit.getName();
		this.contents = kit.getContents();
		this.armourContents = kit.getArmourContents();
	}
}
