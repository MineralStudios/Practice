package gg.mineral.practice.commands.tournament;

import gg.mineral.practice.commands.PlayerCommand;
import gg.mineral.practice.entity.PlayerStatus;

import gg.mineral.practice.managers.EventManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.TournamentManager;

import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.messages.impl.UsageMessages;
import lombok.val;

public class JoinCommand extends PlayerCommand {

    public JoinCommand() {
        super("join");
    }

    @Override
    public void execute(org.bukkit.entity.Player player, String[] args) {
        val profile = ProfileManager.getOrCreateProfile(player);

        if (args.length < 1) {
            profile.message(UsageMessages.JOIN);
            return;
        }

        if (profile.isInTournament()) {
            profile.message(ErrorMessages.ALREADY_IN_TOURNAMENT);
            return;
        }

        if (profile.isInEvent()) {
            profile.message(ErrorMessages.ALREADY_IN_EVENT);
            return;
        }

        if (profile.getPlayerStatus() != PlayerStatus.IDLE) {
            profile.message(ErrorMessages.YOU_ARE_NOT_IN_LOBBY);
            return;
        }

        val event = EventManager.getEventByName(args[0]);

        if (event != null)
            event.addPlayer(profile);

        val tournament = TournamentManager.getTournamentByName(args[0]);

        if (tournament != null)
            tournament.addPlayer(profile);

        if (event == null && tournament == null)
            profile.message(ErrorMessages.EVENT_TOURNAMENT_NOT_EXIST);
    }
}
