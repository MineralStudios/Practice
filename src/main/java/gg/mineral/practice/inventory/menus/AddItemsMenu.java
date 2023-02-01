package gg.mineral.practice.inventory.menus;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.inventory.PracticeMenu;

public class AddItemsMenu extends PracticeMenu {
	static List<Material> EXCLUDED = Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.MUSHROOM_SOUP, Material.POTION,
			Material.GOLDEN_APPLE, Material.ENDER_PEARL, Material.WATER_BUCKET, Material.LAVA_BUCKET),
			INCLUDED = Arrays.asList(Material.COOKED_BEEF, Material.GOLDEN_CARROT, Material.GRILLED_PORK);

	final static String TITLE = CC.BLUE + "Add Items";

	public AddItemsMenu() {
		super(TITLE);
		setClickCancelled(true);
	}

	@Override
	public boolean update() {

		for (ItemStack is : viewer.getKitEditor().getQueueEntry().getGametype().getKit().getContents()) {

			if (is == null)
				continue;

			Material i = is.getType();

			if (EXCLUDED.contains(i))
				continue;

			if (contains(is))
				continue;

			if (INCLUDED.contains(i)) {
				for (Material material : INCLUDED) {
					ItemStack item = new ItemStack(material, 64);
					add(item, () -> viewer.getInventory().addItem(item));
				}

				return true;
			}

			add(is, () -> viewer.getInventory().addItem(is));
		}

		return true;
	}
}
