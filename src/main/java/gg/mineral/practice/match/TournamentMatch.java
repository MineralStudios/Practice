package gg.mineral.practice.match;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard;
import gg.mineral.practice.tournaments.Tournament;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.Strings;
import gg.mineral.practice.util.messages.impl.TextComponents;
import lombok.val;
import org.bukkit.Bukkit;

public class TournamentMatch extends Match {

    Tournament tournament;

    public TournamentMatch(Profile profile1, Profile profile2, MatchData matchData, Tournament tournament) {
        super(profile1, profile2, matchData);
        this.tournament = tournament;
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        stat(attacker, collector -> collector.end(true));
        stat(victim, collector -> collector.end(false));

        deathAnimation(attacker, victim);

        stat(attacker, this::setInventoryStats);
        stat(victim, this::setInventoryStats);

        val winMessage = getWinMessage(attacker);
        val loseMessage = getLoseMessage(victim);

        for (val profile : getParticipants()) {
            profile.getPlayer().sendMessage(CC.SEPARATOR);
            profile.getPlayer().sendMessage(Strings.MATCH_RESULTS);
            profile.getPlayer().spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage);
            profile.getPlayer().sendMessage(CC.SEPARATOR);
        }

        resetPearlCooldown(attacker, victim);
        attacker.setScoreboard(MatchEndScoreboard.INSTANCE);
        victim.setScoreboard(DefaultScoreboard.INSTANCE);
        MatchManager.remove(this);

        victim.heal();
        victim.removePotionEffects();
        sendBackToLobby(victim);

        Bukkit.getServer().getScheduler().runTaskLater(PracticePlugin.INSTANCE, () -> {
            attacker.setScoreboard(DefaultScoreboard.INSTANCE);

            tournament.removePlayer(victim);
            tournament.removeMatch(TournamentMatch.this);

            attacker.teleportToLobby();

            if (!tournament.isEnded()) {
                attacker.setPlayerStatus(PlayerStatus.IDLE);
                attacker.getInventory().setInventoryForTournament();
            } else
                attacker.getInventory().setInventoryForLobby();

            if (attacker.getMatch().equals(this))
                attacker.removeFromMatch();

        }, getPostMatchTime());

        for (val spectator : getSpectators()) {
            spectator.getPlayer().sendMessage(CC.SEPARATOR);
            spectator.getPlayer().sendMessage(Strings.MATCH_RESULTS);
            spectator.getPlayer().spigot().sendMessage(winMessage, TextComponents.SPLITTER, loseMessage);
            spectator.getPlayer().sendMessage(CC.SEPARATOR);
            spectator.getSpectateHandler().stopSpectating();
        }

        clearWorld();
    }
}
