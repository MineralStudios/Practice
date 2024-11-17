package gg.mineral.practice.match;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.PlayerStatus;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.events.Event;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.MatchManager;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.scoreboard.impl.DefaultScoreboard;
import gg.mineral.practice.scoreboard.impl.MatchEndScoreboard;
import gg.mineral.practice.util.CoreConnector;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.messages.CC;
import gg.mineral.practice.util.messages.impl.Strings;
import gg.mineral.practice.util.messages.impl.TextComponents;
import lombok.val;

public class EventMatch extends Match {

    Event event;

    public EventMatch(Profile profile1, Profile profile2, MatchData matchData, Event event) {
        super(profile1, profile2, matchData);
        this.event = event;
    }

    @Override
    public void setupLocations(Location location1, Location location2) {
        setWorldParameters(location1.getWorld());
    }

    @Override
    public void end(Profile attacker, Profile victim) {
        stat(attacker, collector -> collector.end(true));
        stat(victim, collector -> collector.end(false));

        deathAnimation(attacker, victim);

        stat(attacker, collector -> setInventoryStats(collector));
        stat(victim, collector -> setInventoryStats(collector));

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
            if (attacker.getPlayerStatus() == PlayerStatus.FIGHTING && !attacker.getMatch().isEnded())
                return;
            attacker.setScoreboard(DefaultScoreboard.INSTANCE);
            event.removePlayer(victim);
            event.removeMatch(EventMatch.this);

            val eventArena = ArenaManager.getArenas().get(event.getEventArenaId());

            if (!event.isEnded()) {
                PlayerUtil.teleport(attacker.getPlayer(), eventArena.getWaitingLocation());
                attacker.setPlayerStatus(PlayerStatus.IDLE);
                attacker.getInventory().setInventoryForEvent();
            } else {
                attacker.teleportToLobby();
                attacker.getInventory().setInventoryForLobby();
            }

            if (attacker.getMatch().equals(this))
                attacker.removeFromMatch();

            if (CoreConnector.connected()) {
                // CoreConnector.INSTANCE.getNameTagAPI().giveTagAfterMatch(profile1.getPlayer(),
                // profile2.getPlayer());
            }

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
