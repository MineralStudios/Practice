package ms.uk.eclipse.commands.tournament;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.core.utils.message.ErrorMessage;
import ms.uk.eclipse.core.utils.message.UsageMessage;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.TournamentManager;
import ms.uk.eclipse.tournaments.Tournament;
import ms.uk.eclipse.util.messages.ErrorMessages;

public class JoinCommand extends PlayerCommand {

    final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
    final TournamentManager tournamentManager = PracticePlugin.INSTANCE.getTournamentManager();

    public JoinCommand() {
        super("join");
    }

    @Override
    public void execute(org.bukkit.entity.Player player, String[] args) {
        Profile p = playerManager.getProfile(player);

        if (args.length <= 0) {
            p.message(new UsageMessage("/join <Name>"));
            return;
        }

        if (p.getPlayerStatus() == PlayerStatus.IN_TOURAMENT) {
            p.message(new ErrorMessage("You are already in a tournament"));
            return;
        }

        if (p.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
            p.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        Tournament t = tournamentManager.getTournamentByName(args[0]);

        if (t == null) {
            return;
        }

        t.addPlayer(p);
    }
}
