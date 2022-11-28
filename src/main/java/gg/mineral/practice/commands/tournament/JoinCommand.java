package gg.mineral.practice.commands.tournament;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.managers.TournamentManager;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.messages.ErrorMessages;
import gg.mineral.practice.util.messages.UsageMessages;

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
