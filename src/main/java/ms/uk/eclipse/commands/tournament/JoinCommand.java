package ms.uk.eclipse.commands.tournament;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.commands.PlayerCommand;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.entity.PlayerStatus;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.managers.TournamentManager;
import ms.uk.eclipse.tournaments.Tournament;
import ms.uk.eclipse.util.messages.ErrorMessages;
import ms.uk.eclipse.util.messages.UsageMessages;

public class JoinCommand extends PlayerCommand {

    final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
    final TournamentManager tournamentManager = PracticePlugin.INSTANCE.getTournamentManager();

    public JoinCommand() {
        super("join");
    }

    @Override
    public void execute(org.bukkit.entity.Player player, String[] args) {
        Profile p = playerManager.getProfile(player);

        if (args.length < 1) {
            p.message(UsageMessages.JOIN);
            return;
        }

        if (p.getPlayerStatus() == PlayerStatus.IN_TOURAMENT) {
            p.message(ErrorMessages.ALREADY_IN_TOURNAMENT);
            return;
        }

        if (p.getPlayerStatus() != PlayerStatus.IN_LOBBY) {
            p.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        Tournament t = tournamentManager.getTournamentByName(args[0]);

        if (t == null) {
            p.message(ErrorMessages.TOURNAMENT_NOT_EXIST);
            return;
        }

        t.addPlayer(p);
    }
}
