package gg.mineral.practice.match.data;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.math.MathUtil;
import gg.mineral.practice.util.messages.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchStatisticCollector {
    final Profile profile;
    @Getter
    Integer hitCount, currentCombo, longestCombo, averageCombo, highestCps, wTapCount, wTapAccuracy,
            potionsThrown, potionsMissed, potionsStolen, potionsRemaining, potionAccuracy, soupsRemaining,
            remainingHealth;
    int clicks;
    long clickCounterStart;
    @Getter
    ItemStack[] inventoryContents;
    @Getter
    ItemStack helmet, chestplate, leggings, boots;
    @Getter
    List<String> potionEffectStrings;

    public void start() {
        hitCount = 0;
        currentCombo = 0;
        longestCombo = 0;
        averageCombo = 0;
        highestCps = 0;
        wTapCount = 0;
        wTapAccuracy = 0;
        potionsThrown = 0;
        potionsMissed = 0;
        potionsStolen = 0;
        potionsRemaining = 0;
        potionAccuracy = 0;
        soupsRemaining = 0;
        clicks = 0;
        clickCounterStart = System.currentTimeMillis();
        potionEffectStrings = new GlueList<String>();
    }

    public void end() {
        this.potionsRemaining = profile.getInventory().getNumber(Material.POTION, (short) 16421);
        this.soupsRemaining = profile.getInventory().getNumber(Material.MUSHROOM_SOUP);
        this.remainingHealth = profile.getPlayer().isDead() ? 0 : (int) profile.getPlayer().getHealth();
        this.potionAccuracy = (int) (100 - (getPotionsMissed() * 100D
                / getPotionsThrown()));
        this.wTapAccuracy = (int) (getHitCount() * 100D
                / getWTapCount());
        this.inventoryContents = profile.getInventory().getContents();
        this.helmet = profile.getInventory().getHelmet();
        this.chestplate = profile.getInventory().getChestplate();
        this.leggings = profile.getInventory().getLeggings();
        this.boots = profile.getInventory().getBoots();

        for (final PotionEffect potionEffect : profile.getPlayer().getActivePotionEffects()) {
            final String romanNumeral = MathUtil.convertToRomanNumeral(potionEffect.getAmplifier() + 1);
            final String effectName = StringUtil.toNiceString(potionEffect.getType().getName().toLowerCase());
            final String duration = MathUtil.convertTicksToMinutes(potionEffect.getDuration());
            potionEffectStrings.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + effectName
                    + " " + romanNumeral + ChatColor.GRAY + " (" + duration + ")");
        }
    }

    public void increaseHitCount() {
        hitCount++;
        currentCombo++;

        if (profile.getPlayer().getHandle().isExtraKnockback()) {
            wTapCount++;
        }

        if (currentCombo > 1) {
            averageCombo += currentCombo;
            averageCombo /= 2;
        }

        longestCombo = Math.max(currentCombo, longestCombo);
    }

    public void resetCombo() {
        currentCombo = 0;
    }

    public void clearHitCount() {
        hitCount = 0;
    }

    public void thrownPotion(boolean missed) {
        potionsThrown++;

        if (missed) {
            potionsMissed++;
        }
    }

    public void stolenPotion() {
        potionsStolen++;
    }

    public void click() {
        if (System.currentTimeMillis() - clickCounterStart > 1000) {
            clickCounterStart = System.currentTimeMillis();
            highestCps = Math.max(clicks, highestCps);
            clicks = 1;
        }

        clicks++;
    }

    public String[] getPotionEffectStringArray() {
        return potionEffectStrings.toArray(new String[0]);
    }

}
