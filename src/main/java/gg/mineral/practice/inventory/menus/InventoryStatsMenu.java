package gg.mineral.practice.inventory.menus;

import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.Menu;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.match.data.MatchStatisticCollector;
import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@ClickCancelled(true)
@Getter
@RequiredArgsConstructor
public class InventoryStatsMenu extends PracticeMenu {
    private final String opponent;
    private final MatchStatisticCollector matchStatisticCollector;
    @Nullable
    @Setter
    private Menu previousMenu = null;

    @Override
    public void update() {
        boolean hasPreviousMenu = previousMenu != null;
        if (opponent != null && !hasPreviousMenu)
            setSlot(53, ItemStacks.VIEW_OPPONENT_INVENTORY,
                    interaction -> interaction.getProfile().getPlayer()
                            .performCommand("viewinventory " + opponent));

        setContents(matchStatisticCollector.getInventoryContents());
        setSlot(36, matchStatisticCollector.getHelmet());
        setSlot(37, matchStatisticCollector.getChestplate());
        setSlot(38, matchStatisticCollector.getLeggings());
        setSlot(39, matchStatisticCollector.getBoots());

        if (hasPreviousMenu)
            setSlot(45, ItemStacks.BACK, interaction -> interaction.getProfile().openMenu(previousMenu));

        setSlot(hasPreviousMenu ? 48 : 45, !matchStatisticCollector.isAlive() ? ItemStacks.NO_HEALTH
                : ItemStacks.HEALTH
                        .name(CC.SECONDARY + CC.B + "Health")
                        .lore(" ", CC.WHITE + "Remaining:",
                                CC.GOLD + matchStatisticCollector.getRemainingHealth())
                        .amount(matchStatisticCollector.getRemainingHealth()).build());

        setSlot(hasPreviousMenu ? 49 : 46, ItemStacks.HEALTH_POTIONS_LEFT
                .lore(" ", CC.WHITE + "Thrown: " + CC.GOLD + matchStatisticCollector.getPotionsThrown(),
                        CC.WHITE + "Missed: " + CC.GOLD
                                + matchStatisticCollector.getPotionsMissed(),
                        CC.WHITE + "Stolen: " + CC.GOLD
                                + matchStatisticCollector.getPotionsStolen(),
                        CC.WHITE + "Accuracy: " + CC.GOLD
                                + matchStatisticCollector.getPotionAccuracy() + "%")
                .amount(Math.max(matchStatisticCollector.getPotionsRemaining(), 1)).build());

        setSlot(hasPreviousMenu ? 50 : 47, ItemStacks.SOUP_LEFT
                .amount(Math.max(matchStatisticCollector.getSoupsRemaining(), 1)).build());

        setSlot(hasPreviousMenu ? 51 : 48, ItemStacks.HITS
                .name(CC.SECONDARY + CC.B + matchStatisticCollector.getHitCount() + " Hits")
                .lore(CC.WHITE + "Longest Combo: " + CC.GOLD
                        + matchStatisticCollector.getLongestCombo(),
                        CC.WHITE + "Average Combo: " + CC.GOLD
                                + matchStatisticCollector.getAverageCombo(),
                        CC.WHITE + "W Tap Accuracy: " + CC.GOLD
                                + matchStatisticCollector.getWTapAccuracy() + "%")
                .build());

        setSlot(hasPreviousMenu ? 52 : 49, ItemStacks.CLICKS
                .name(CC.SECONDARY + CC.B + "Highest CPS: " + matchStatisticCollector.getHighestCps())
                .build());

        setSlot(hasPreviousMenu ? 53 : 50, ItemStacks.POTION_EFFECTS
                .lore(matchStatisticCollector.getPotionEffectStringArray()).build());
    }

    @Override
    public String getTitle() {
        return CC.BLUE + matchStatisticCollector.getProfile().getName();
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }
}
