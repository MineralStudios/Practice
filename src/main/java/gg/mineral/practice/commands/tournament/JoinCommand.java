package gg.mineral.practice.commands.tournament;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.managers.EventManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.TournamentManager;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;

public class JoinCommand extends PlayerCommand {

    public JoinCommand() {
        super("join");
    }

    @Override
    public void execute(org.bukkit.entity.Player player, String[] args) {
        Profile p = ProfileManager.getOrCreateProfile(player);

        if (args.length < 1) {
            p.message(UsageMessages.JOIN);
            return;
        }

        if (p.isInTournament()) {
            p.message(ErrorMessages.ALREADY_IN_TOURNAMENT);
            return;
        }

        if (p.getPlayerStatus() != PlayerStatus.IDLE) {
            p.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        Event event = EventManager.getEventByName(args[0]);

        if (event != null) {
            event.addPlayer(p);
        }

        Tournament tournament = TournamentManager.getTournamentByName(args[0]);

        if (tournament == null) {
            p.message(ErrorMessages.TOURNAMENT_NOT_EXIST);
            return;
        }

        tournament.addPlayer(p);
    }
}
