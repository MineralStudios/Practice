package gg.mineral.practice.tournaments;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.bukkit.events.PlayerTournamentInitializeEvent;
import gg.mineral.practice.bukkit.events.PlayerTournamentStartEvent;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.managers.TournamentManager;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.TournamentMatch;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Tournament {
    GlueList<Match> matches = new GlueList<>();
    ProfileList players = new ProfileList();
    boolean started = false;
    @Getter
    boolean ended = false;
    MatchData matchData;
    int round = 1;
    @Getter
    String host;

    public Tournament(Profile p) {
        this.matchData = new MatchData(p.getDuelSettings());
        this.host = p.getName();
        addPlayer(p);
    }

    public void addPlayer(Profile p) {

        if (started) {
            p.message(ErrorMessages.TOURNAMENT_STARTED);
            return;
        }

        p.setTournament(this);
        players.add(p);

        val joinedMessage = ChatMessages.JOINED_TOURNAMENT.clone().replace("%player%", p.getName());
        ProfileManager.broadcast(players, joinedMessage);
    }

    public void removePlayer(Profile p) {
        players.remove(p);
        p.setTournament(null);

        val leftMessage = ChatMessages.LEFT_TOURNAMENT.clone().replace("%player%", p.getName());
        ProfileManager.broadcast(players, leftMessage);

        if (players.isEmpty()) {
            TournamentManager.remove(this);
            ended = true;
            return;
        }

        if (started && players.size() == 1) {
            val winner = players.getFirst();
            winner.removeFromTournament();

            TournamentManager.remove(this);
            ended = true;

            val wonMessage = ChatMessages.WON_TOURNAMENT.clone().replace("%player%", winner.getName());

            ProfileManager.broadcast(wonMessage);

        }
    }

    public void start() {

        if (started)
            return;

        val bukkitHost = players.getFirst().getPlayer();

        val event = new PlayerTournamentInitializeEvent(30, bukkitHost);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        TournamentManager.registerTournament(this);

        val messageToBroadcast = ChatMessages.BROADCAST_TOURNAMENT.clone()
                .replace("%player%", host).setTextEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + host),
                        ChatMessages.CLICK_TO_JOIN);

        ProfileManager.broadcast(messageToBroadcast);

        new BukkitRunnable() {
            @Override
            public void run() {
                started = true;

                val event = new PlayerTournamentStartEvent(bukkitHost);
                Bukkit.getPluginManager().callEvent(event);

                if (players.size() == 1 || event.isCancelled()) {
                    Profile winner = players.getFirst();
                    winner.removeFromTournament();

                    ErrorMessages.TOURNAMENT_NOT_ENOUGH_PLAYERS.send(winner.getPlayer());
                    TournamentManager.remove(Tournament.this);
                    ended = true;
                    return;
                }

                startRound();
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 600);
    }

    public void startRound() {

        if (players.size() == 1) {
            Profile winner = players.getFirst();
            winner.removeFromTournament();
            ended = true;
            TournamentManager.remove(this);
            return;
        }

        val iter = players.iterator();

        while (iter.hasNext()) {
            val p1 = iter.next();

            if (!iter.hasNext()) {
                ChatMessages.NO_OPPONENT.send(p1.getPlayer());
                return;
            }

            val p2 = iter.next();

            val match = new TournamentMatch(p1, p2, matchData, this);
            match.start();
            matches.add(match);
        }
    }

    public void removeMatch(Match m) {
        matches.remove(m);

        if (ended)
            return;

        if (matches.isEmpty()) {
            val broadcastedMessage = ChatMessages.ROUND_OVER.clone().replace("%round%", "" + round);

            ProfileManager.broadcast(players, broadcastedMessage);

            new BukkitRunnable() {
                @Override
                public void run() {
                    startRound();
                    round++;
                }
            }.runTaskLater(PracticePlugin.INSTANCE, 100);
        }
    }
}
