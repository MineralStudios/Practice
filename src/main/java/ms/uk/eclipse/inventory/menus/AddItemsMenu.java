package ms.uk.eclipse.inventory.menus;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import land.strafe.api.collection.GlueList;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.core.utils.message.StrikingMessage;
import ms.uk.eclipse.inventory.Menu;

public class AddItemsMenu extends Menu {
	static List<Material> EXCLUDED = new GlueList<>(Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE,
			Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS, Material.MUSHROOM_SOUP, Material.POTION,
			Material.GOLDEN_APPLE, Material.ENDER_PEARL, Material.WATER_BUCKET, Material.LAVA_BUCKET));

	static List<Material> INCLUDED = new GlueList<>(
			Arrays.asList(Material.COOKED_BEEF, Material.GOLDEN_CARROT, Material.GRILLED_PORK));

	public AddItemsMenu() {
		super(new StrikingMessage("Add Items", CC.PRIMARY, true));
		setClickCancelled(true);
	}

	@Override
	public boolean update() {

		for (ItemStack is : viewer.getKitEditorData().getGametype().getKit().getContents()) {

			if (is == null) {
				continue;
			}

			Material i = is.getType();

			if (EXCLUDED.contains(i)) {
				continue;
			}

			if (contains(is)) {
				continue;
			}

			Runnable runnable = () -> viewer.getInventory().addItem(is);

			if (INCLUDED.contains(i)) {
				for (Material m : INCLUDED) {
					ItemStack item = new ItemStack(m, 64);

					runnable = () -> viewer.getInventory().addItem(is);

					add(item, runnable);
				}
				return true;
			}

			add(is, runnable);
		}

		return true;
	}
}
