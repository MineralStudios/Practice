package gg.mineral.practice.kit;

import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Kit {
	@Getter
	String name;
	@Getter
	@Setter
	ItemStack[] contents = new ItemStack[36], armourContents = new ItemStack[4];

	public Kit(ItemStack[] contents, ItemStack[] armourContents) {
		this("Custom", contents, armourContents);
	}

	public Kit(Kit kit) {
		this.name = kit.getName();
		this.contents = kit.getContents();
		this.armourContents = kit.getArmourContents();
	}
}
