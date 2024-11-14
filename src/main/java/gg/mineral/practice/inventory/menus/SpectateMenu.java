package gg.mineral.practice.inventory.menus;

import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.inventory.ClickCancelled;
import gg.mineral.practice.inventory.PracticeMenu;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.util.items.ItemBuilder;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.val;

@ClickCancelled(true)
public class SpectateMenu extends PracticeMenu {

    @Override
    public void update() {
        clear();
        for (val m : MatchManager.getMatches()) {
            val item = m.getData().getGametype().getDisplayItem().clone();

            if (m.getProfile1() == null || m.getProfile2() == null)
                continue;

            val skull = new ItemBuilder(item.clone())
                    .name(CC.SECONDARY + CC.B + m.getProfile1().getName() + " vs " + m.getProfile2().getName())
                    .lore(
                            CC.WHITE + "Game type:",
                            CC.GOLD + m.getData().getGametype().getName(),
                            CC.BOARD_SEPARATOR, CC.ACCENT + "Click to spectate.")
                    .build();
            add(skull, interaction -> {
                val profile = interaction.getProfile();

                val profileToSpectate = ProfileManager
                        .getProfile(m.getParticipants().getFirst().getUuid(),
                                p -> p.getPlayerStatus() == PlayerStatus.FIGHTING || p.isInEvent());

                if (profileToSpectate == null) {
                    profile.message(ErrorMessages.PLAYER_NOT_IN_MATCH_OR_EVENT);
                    return;
                }

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
