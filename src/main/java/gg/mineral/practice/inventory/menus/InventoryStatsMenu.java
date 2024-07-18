package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.util.items.ItemStacks;
import gg.mineral.practice.util.messages.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.match.data.MatchStatisticCollector;

@ClickCancelled(true)
@Getter
@RequiredArgsConstructor
public class InventoryStatsMenu extends PracticeMenu {
    private final Profile profile;
    private final String opponent;
    private final MatchStatisticCollector matchStatisticCollector;

    public InventoryStatsMenu(InventoryStatsMenu m) {
        super(m);
        this.profile = m.getProfile();
        this.opponent = m.getOpponent();
        this.matchStatisticCollector = m.getMatchStatisticCollector();
    }

    @Override
    public void update() {
        if (opponent != null)
            setSlot(53, ItemStacks.VIEW_OPPONENT_INVENTORY,
                    interaction -> interaction.getProfile().getPlayer().performCommand("viewinventory " + opponent));

        setContents(matchStatisticCollector.getInventoryContents());
        setSlot(36, matchStatisticCollector.getHelmet());
        setSlot(37, matchStatisticCollector.getChestplate());
        setSlot(38, matchStatisticCollector.getLeggings());
        setSlot(39, matchStatisticCollector.getBoots());

        setSlot(45, !matchStatisticCollector.isAlive() ? ItemStacks.NO_HEALTH
                : ItemStacks.HEALTH
                        .name(CC.SECONDARY + CC.B + "Health")
                        .lore(" ", CC.WHITE + "Remaining:", CC.GOLD + matchStatisticCollector.getRemainingHealth())
                        .amount(matchStatisticCollector.getRemainingHealth()).build());

        setSlot(46, ItemStacks.HEALTH_POTIONS_LEFT
                .lore(" ", CC.WHITE + "Thrown: " + CC.GOLD + matchStatisticCollector.getPotionsThrown(),
                        CC.WHITE + "Missed: " + CC.GOLD + matchStatisticCollector.getPotionsMissed(),
                        CC.WHITE + "Stolen: " + CC.GOLD + matchStatisticCollector.getPotionsStolen(),
                        CC.WHITE + "Accuracy: " + CC.GOLD + matchStatisticCollector.getPotionAccuracy() + "%")
                .amount(Math.max(matchStatisticCollector.getPotionsRemaining(), 1)).build());

        setSlot(47, ItemStacks.SOUP_LEFT
                .amount(Math.max(matchStatisticCollector.getSoupsRemaining(), 1)).build());

        setSlot(48, ItemStacks.HITS
                .name(CC.SECONDARY + CC.B + profile.getMatchStatisticCollector().getHitCount() + " Hits")
                .lore(CC.WHITE + "Longest Combo: " + CC.GOLD + matchStatisticCollector.getLongestCombo(),
                        CC.WHITE + "Average Combo: " + CC.GOLD + matchStatisticCollector.getAverageCombo(),
                        CC.WHITE + "W Tap Accuracy: " + CC.GOLD + matchStatisticCollector.getWTapAccuracy() + "%")
                .build());

        setSlot(49, ItemStacks.CLICKS
                .name(CC.SECONDARY + CC.B + "Highest CPS: " + matchStatisticCollector.getHighestCps()).build());

        setSlot(50, ItemStacks.POTION_EFFECTS
                .lore(matchStatisticCollector.getPotionEffectStringArray()).build());
    }

    @Override
    public String getTitle() {
        return CC.BLUE + profile.getName();
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }
}
