package gg.mineral.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import gg.mineral.practice.util.items.ItemStacks;

public class ComsumeListener implements Listener {
	@EventHandler
	public void onConsume(final PlayerItemConsumeEvent e) {
		switch (e.getItem().getType()) {
			case POTION:
				e.setReplacement(ItemStacks.AIR);
				break;
			case GOLDEN_APPLE:
				final ItemStack goldenHead = e.getItem();
				try {
					if (goldenHead.getItemMeta().getDisplayName().equalsIgnoreCase("Golden Head")) {
						final Player p = e.getPlayer();
						p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
						p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1600, 0));
					}
				} catch (Exception ex) {
				}
				break;
			default:
		}
	}
}
