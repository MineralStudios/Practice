package gg.mineral.practice.inventory.menus;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.val;

@ClickCancelled(true)
public class AddItemsMenu extends PracticeMenu {
	@SuppressWarnings("deprecation")
	private static final IntSet LIMITED = IntOpenHashSet.of(Material.DIAMOND_HELMET.getId(),
			Material.DIAMOND_CHESTPLATE.getId(),
			Material.DIAMOND_LEGGINGS.getId(), Material.DIAMOND_BOOTS.getId(), Material.MUSHROOM_SOUP.getId(),
			Material.POTION.getId(),
			Material.GOLDEN_APPLE.getId(), Material.ENDER_PEARL.getId(), Material.WATER_BUCKET.getId(),
			Material.LAVA_BUCKET.getId(), Material.ARROW.getId());
	@SuppressWarnings("deprecation")
	private static final IntSet INCLUDED = IntOpenHashSet.of(Material.COOKED_BEEF.getId(),
			Material.GOLDEN_CARROT.getId(),
			Material.GRILLED_PORK.getId());

	@SuppressWarnings("deprecation")
	@Override
	public void update() {

		val maxAmountMap = new Object2IntOpenHashMap<String>();

		for (val is : viewer.getKitEditor().getGametype().getKit().getContents()) {
			if (is == null || !LIMITED.contains(is.getType().getId()))
				continue;

			val string = is.getType() + ":" + is.getDurability();
			int maxAmount = maxAmountMap.getOrDefault(string, 0);
			maxAmountMap.put(string, maxAmount + is.getAmount());
		}

		for (val is : viewer.getKitEditor().getGametype().getKit().getContents()) {

			if (is == null)
				continue;

			val type = is.getType();

			if (contains(is))
				continue;

			if (INCLUDED.contains(type.getId())) {
				for (val material : INCLUDED) {
					val item = new ItemStack(material, 64);
					add(item, interaction -> interaction.getProfile().getPlayer().setItemOnCursor(item));
				}

				continue;
			}

			if (LIMITED.contains(type.getId())) {
				add(is, interaction -> {
					val string = is.getType() + ":" + is.getDurability();
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
