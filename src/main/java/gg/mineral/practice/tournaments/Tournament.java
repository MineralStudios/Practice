package gg.mineral.practice.tournaments;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.managers.TournamentManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.TournamentMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import net.md_5.bungee.api.chat.ClickEvent;

public class Tournament {
    GlueList<Match> matches = new GlueList<>();
    ProfileList players = new ProfileList();

    boolean started = false;
    boolean ended = false;
    MatchData matchData;
    int round = 1;
    String host;

    public Tournament(Profile p) {
        this.matchData = p.getMatchData();
        this.host = p.getName();
        addPlayer(p);
        TournamentManager.registerTournament(this);
    }

    public void addPlayer(Profile p) {

        if (started) {
            p.message(ErrorMessages.TOURNAMENT_STARTED);
            return;
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

            TournamentManager.remove(this);
            ended = true;

            ChatMessage wonMessage = ChatMessages.WON_TOURNAMENT.clone().replace("%player%", winner.getName());

            PlayerManager.broadcast(PlayerManager.getProfiles(),
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

        PlayerManager.broadcast(PlayerManager.getProfiles(), messageToBroadcast);

        Tournament tournament = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                started = true;

                if (players.size() == 1) {
                    Profile winner = players.get(0);
                    winner.removeFromTournament();

                    ErrorMessages.TOURNAMENT_NOT_ENOUGH_PLAYERS.send(winner.bukkit());
                    TournamentManager.remove(tournament);
                    ended = true;
                    return;
                }

                startRound();
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 600);
    }

    public void startRound() {

        if (players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromTournament();
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
                    startRound();
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
}
