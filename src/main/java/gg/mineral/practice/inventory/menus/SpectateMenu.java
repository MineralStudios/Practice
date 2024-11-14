package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import lombok.val;

@ClickCancelled(true)
public class SpectateMenu extends PracticeMenu {

    @Override
    public void update() {
        clear();
        for (val m : MatchManager.getMatches()) {
            val item = m.getData().getGametype().getDisplayItem().clone();
            val gametype = m.getData().getGametype();

            if (m.getProfile1() == null || m.getProfile2() == null || gametype == null)
                continue;

            val skull = new ItemBuilder(item.clone())
                    .name(CC.SECONDARY + CC.B + m.getProfile1().getName() + " vs " + m.getProfile2().getName())
                    .lore(
                            CC.WHITE + "Game type:",
                            CC.GOLD + gametype.getName(),
                            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to spectate.")
                    .build();
            add(skull, interaction -> {
                val profile = interaction.getProfile();

                val profileToSpectate = m.getParticipants().getFirst();
                if (profileToSpectate != null)
                    profile.getSpectateHandler().spectate(profileToSpectate);
            });
        }
    }

    @Override
    public String getTitle() {
        return CC.BLUE + "Spectate";
    }

    @Override
    public boolean shouldUpdate() {
        return true;
    }
}
