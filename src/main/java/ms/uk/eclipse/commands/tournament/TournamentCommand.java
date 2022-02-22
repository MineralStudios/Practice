package ms.uk.eclipse.commands.tournament;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.inventory.SubmitAction;
import ms.uk.eclipse.inventory.menus.SelectModeMenu;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class TournamentCommand extends PlayerCommand {
    final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

    public TournamentCommand() {
        super("tournament");
    }

    @Override
    public void execute(org.bukkit.entity.Player pl, String[] args) {
        Profile player = playerManager.getProfile(pl);

        if (player.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
            player.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        player.openMenu(new SelectModeMenu(SubmitAction.TOURNAMENT));
    }
}
