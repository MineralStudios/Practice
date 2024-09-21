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
    int hitCount, currentCombo, longestCombo, averageCombo, highestCps, wTapCount, wTapAccuracy,
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
    boolean active = false;
    @Getter
    boolean alive = false;

    public void start() {
        if (active)
            throw new IllegalStateException("Already started");

        active = true;
        currentCombo = hitCount = longestCombo = averageCombo = highestCps = wTapCount = wTapAccuracy = potionsThrown = potionsMissed = potionsStolen = potionsRemaining = potionAccuracy = soupsRemaining = clicks = 0;
        clickCounterStart = System.currentTimeMillis();
        potionEffectStrings = new GlueList<String>();
    }

    public void end(boolean alive) {
        if (!active)
            throw new IllegalStateException("Not been started yet.");

        active = false;
        this.alive = alive;
        this.potionsRemaining = profile.getInventory().getNumber(Material.POTION, (short) 16421);
        this.soupsRemaining = profile.getInventory().getNumber(Material.MUSHROOM_SOUP);
        this.remainingHealth = profile.getPlayer().isDead() ? 0 : (int) profile.getPlayer().getHealth();
        this.potionAccuracy = (int) (100 - (getPotionsMissed() * 100D
                / getPotionsThrown()));
        this.wTapAccuracy = (int) (getWTapCount() * 100D
                / getHitCount());
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
        if (!active)
            return;

        hitCount++;
        currentCombo++;

        if (profile.getPlayer().getHandle().isSprinting())
            wTapCount++;

        if (currentCombo > 1) {
            averageCombo += currentCombo;
            averageCombo /= 2;
        }

        longestCombo = Math.max(currentCombo, longestCombo);
    }

    public void resetCombo() {
        if (!active)
            return;

        currentCombo = 0;
    }

    public void clearHitCount() {
        if (!active)
            return;

        hitCount = 0;
    }

    public void thrownPotion(boolean missed) {
        if (!active)
            return;

        potionsThrown++;

        if (missed)
            potionsMissed++;
    }

    public void stolenPotion() {
        if (!active)
            return;

        potionsStolen++;
    }

    public void click() {
        if (!active)
            return;

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
