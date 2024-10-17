package gg.mineral.practice.inventory.menus;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

@ClickCancelled(true)
public class AddItemsMenu extends PracticeMenu {
	static List<Material> LIMITED = Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.MUSHROOM_SOUP, Material.POTION,
			Material.GOLDEN_APPLE, Material.ENDER_PEARL, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.ARROW),
			INCLUDED = Arrays.asList(Material.COOKED_BEEF, Material.GOLDEN_CARROT, Material.GRILLED_PORK);

	@Override
	public void update() {

		Object2IntOpenHashMap<String> maxAmountMap = new Object2IntOpenHashMap<String>();

		for (ItemStack is : viewer.getKitEditor().getGametype().getKit().getContents()) {
			if (is == null || !LIMITED.contains(is.getType()))
				continue;

			String string = is.getType() + ":" + is.getDurability();
			int maxAmount = maxAmountMap.getOrDefault(string, 0);
			maxAmountMap.put(string, maxAmount + is.getAmount());
		}

		for (ItemStack is : viewer.getKitEditor().getGametype().getKit().getContents()) {

			if (is == null)
				continue;

			Material i = is.getType();

			if (contains(is))
				continue;

			if (INCLUDED.contains(i)) {
				for (Material material : INCLUDED) {
					ItemStack item = new ItemStack(material, 64);
					add(item, interaction -> interaction.getProfile().getPlayer().setItemOnCursor(item));
				}

				continue;
			}

			if (LIMITED.contains(i)) {
				add(is, interaction -> {
					String string = is.getType() + ":" + is.getDurability();
					int maxAmount = maxAmountMap.getOrDefault(string, 0);

					if (viewer.getInventory().getNumberAndAmount(is.getType(), is.getDurability()) >= maxAmount) {
						viewer.message(ErrorMessages.ITEM_LIMIT);
						return;
					}

					viewer.getPlayer().setItemOnCursor(is);
				});
				continue;
			}

			add(is, interaction -> viewer.getPlayer().setItemOnCursor(is));
		}
	}

	@Override
	public String getTitle() {
		return CC.BLUE + "Add Items";
	}

	@Override
	public boolean shouldUpdate() {
		return false;
	}
}
