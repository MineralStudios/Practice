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
        Profile profile = ProfileManager.getOrCreateProfile(player);

        if (args.length < 1) {
            profile.message(UsageMessages.JOIN);
            return;
        }

        if (profile.isInTournament()) {
            profile.message(ErrorMessages.ALREADY_IN_TOURNAMENT);
            return;
        }

        if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        Event event = EventManager.getEventByName(args[0]);

        if (event != null) {
            event.addPlayer(profile);
        }

        Tournament tournament = TournamentManager.getTournamentByName(args[0]);

        if (tournament == null) {
            profile.message(ErrorMessages.TOURNAMENT_NOT_EXIST);
            return;
        }

        tournament.addPlayer(profile);
    }
}
