package gg.mineral.practice.commands.tournament;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.inventory.SubmitAction;
import gg.mineral.practice.inventory.menus.SelectModeMenu;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class TournamentCommand extends PlayerCommand {

    public TournamentCommand() {
        super("tournament");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        Profile profile = PlayerManager.get(p -> p.getUUID().equals(pl.getUniqueId()));

        if (profile.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        profile.openMenu(new SelectModeMenu(SubmitAction.TOURNAMENT));
    }
}
