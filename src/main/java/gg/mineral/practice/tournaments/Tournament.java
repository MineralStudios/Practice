package gg.mineral.practice.tournaments;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.managers.TournamentManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.MatchData;
import gg.mineral.practice.match.TournamentMatch;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import gg.mineral.practice.util.GlueList;
import net.md_5.bungee.api.chat.ClickEvent;

public class Tournament {
    GlueList<Match> matches = new GlueList<>();
    ProfileList players = new ProfileList();
    ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<>();
    boolean started = false, ended = false, event;
    MatchData matchData;
    int round = 1;
    Arena eventArena;
    String host;
    Location loc;

    public Tournament(Profile p) {
        this.matchData = p.getMatchData();
        this.host = p.getName();
        this.event = false;
        addPlayer(p);
        TournamentManager.register(this);
    }

    public Tournament(Profile profile, Arena eventArena) {
        this.matchData = profile.getMatchData();
        this.host = profile.getName();
        this.event = true;
        this.eventArena = eventArena;
        matchData.setArena(eventArena);
        loc = eventArena.getWaitingLocation();
        addPlayer(profile);
        TournamentManager.register(this);
    }

    public void addPlayer(Profile p) {

        if (started) {
            p.message(ErrorMessages.TOURNAMENT_STARTED);
            return;
        }

        if (event) {
            p.teleport(loc);
        }

        p.setTournament(this);
        players.add(p);

        ChatMessage joinedMessage = ChatMessages.JOINED_TOURNAMENT.clone().replace("%player%", p.getName());
        PlayerManager.broadcast(players, joinedMessage);
    }

    public void removePlayer(Profile p) {
        players.remove(p);

        ChatMessage leftMessage = ChatMessages.LEFT_TOURNAMENT.clone().replace("%player%", p.getName());
        PlayerManager.broadcast(players, leftMessage);

        if (players.size() == 0) {
            TournamentManager.remove(this);
            ended = true;
            return;
        }

        if (started && players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromTournament();

            if (getSpectators().size() > 0) {
                for (Profile pl : getSpectators()) {
                    pl.stopSpectating();
                }
            }

            TournamentManager.remove(this);
            ended = true;

            ChatMessage wonMessage = ChatMessages.WON_TOURNAMENT.clone().replace("%player%", winner.getName());

            PlayerManager.broadcast(PlayerManager.list(),
                    wonMessage);

            return;
        }
    }

    public void start() {

        if (started) {
            return;
        }

        ChatMessage messageToBroadcast = ChatMessages.BROADCAST_TOURNAMENT.clone()
                .replace("%player%", host).setTextEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + host),
                        ChatMessages.CLICK_TO_JOIN);

        PlayerManager.broadcast(PlayerManager.list(), messageToBroadcast);

        Tournament t = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                started = true;

                if (players.size() == 1) {
                    Profile winner = players.get(0);
                    winner.removeFromTournament();
                    if (getSpectators().size() > 0) {
                        for (Profile p : getSpectators()) {
                            p.stopSpectating();
                        }
                    }
                    ErrorMessages.TOURNAMENT_NOT_ENOUGH_PLAYERS.send(winner.bukkit());
                    TournamentManager.remove(t);
                    ended = true;
                    return;
                }

                try {
                    startRound();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 600);
    }

    public void startRound() throws SQLException {

        if (players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromTournament();

            if (getSpectators().size() > 0) {
                for (Profile p : getSpectators()) {
                    p.stopSpectating();
                }
            }

            ended = true;
            TournamentManager.remove(this);
            return;
        }

        Iterator<Profile> iter = players.iterator();

        while (iter.hasNext()) {
            Profile p1 = iter.next();

            if (!iter.hasNext()) {
                ChatMessages.NO_OPPONENT.send(p1.bukkit());
                return;
            }

            Profile p2 = iter.next();

            TournamentMatch match = new TournamentMatch(p1, p2, matchData, this);
            match.start();
            matches.add(match);

            if (event) {
                break;
            }
        }
    }

    public void removeMatch(Match m) {
        matches.remove(m);

        if (ended) {
            return;
        }

        if (matches.isEmpty()) {
            ChatMessage broadcastedMessage = ChatMessages.ROUND_OVER.clone().replace("%round%", "" + round);

            PlayerManager.broadcast(players, broadcastedMessage);

            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        startRound();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    round++;
                }
            }.runTaskLater(PracticePlugin.INSTANCE, 100);
        }
    }

    public String getHost() {
        return host;
    }

    public boolean isEnded() {
        return ended;
    }

    public boolean isEvent() {
        return event;
    }

    public Arena getEventArena() {
        return eventArena;
    }

    public Location getWaitingLocation() {
        return loc;
    }

    public void addSpectator(Profile player) {
        spectators.add(player);
    }

    public ConcurrentLinkedDeque<Profile> getSpectators() {
        return spectators;
    }
}
