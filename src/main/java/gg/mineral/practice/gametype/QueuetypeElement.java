package gg.mineral.practice.gametype;

import java.util.List;

import org.bukkit.inventory.ItemStack;

public interface QueuetypeElement {
    public ItemStack getDisplayItem();

    public String getDisplayName();

    public List<String> getLeaderboardLore();
}
