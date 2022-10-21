package gg.mineral.practice.match;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import gg.mineral.practice.entity.Profile;

public class PostMatchData {
    ItemStack[] inventoryContents;
    int health, hitCount, longestCombo;
    long amountOfPotsAndSoups;
    String opponentName;

    public PostMatchData(Profile profile) {
        this.inventoryContents = profile.getInventory().getContents();
        this.health = (int) profile.bukkit().getHealth();
        this.amountOfPotsAndSoups = profile.getInventory()
                .count(itemstack -> (itemstack.getType() == Material.POTION && itemstack.getDurability() == 16421)
                        || itemstack.getType() == Material.MUSHROOM_SOUP);
        this.longestCombo = profile.getLongestCombo();
        this.opponentName = profile.getMatch().getOpponent(profile).getName();
    }

}
