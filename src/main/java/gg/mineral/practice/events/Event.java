package gg.mineral.practice.events;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.EventManager;
import gg.mineral.practice.managers.PlayerManager;
import gg.mineral.practice.match.EventMatch;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.MatchData;
import gg.mineral.practice.traits.Spectatable;
import gg.mineral.practice.util.ProfileList;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import net.md_5.bungee.api.chat.ClickEvent;

public class Event implements Spectatable {

    GlueList<Match> matches = new GlueList<>();
    final EventManager eventManager = PracticePlugin.INSTANCE.getEventManager();
    MatchData m;
    int round = 1;
    String host;
    boolean started = false;
    boolean ended = false;
    ProfileList players = new ProfileList();
    final PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();
    Arena eventArena;
    Location loc;

    public Event(Profile p, Arena eventArena) {
        this.m = p.getMatchData();
        this.host = p.getName();
        addPlayer(p);
        eventManager.registerEvent(this);
        this.eventArena = eventArena;
        m.setArena(eventArena);
        loc = eventArena.getWaitingLocation();
    }

    public void addPlayer(Profile p) {

        if (started) {
            p.message(ErrorMessages.EVENT_STARTED);
            return;
        }

        p.teleport(loc);
        p.setEvent(this);
        players.add(p);

        ChatMessage joinedMessage = ChatMessages.JOINED_EVENT.clone().replace("%player%", p.getName());
        playerManager.broadcast(players, joinedMessage);
    }

    public void startRound() {

        if (players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromEvent();

            for (Profile p : getSpectators()) {
                p.stopSpectating();
            }

            ended = true;
            eventManager.remove(this);
            return;
        }

        Iterator<Profile> iter = players.iterator();

        Profile p1 = iter.next();

        if (!iter.hasNext()) {
            ChatMessages.NO_OPPONENT.send(p1.bukkit());
            return;
        }

        Profile p2 = iter.next();

        EventMatch match = new EventMatch(p1, p2, m, this);
        match.start();
        matches.add(match);
    }

    public void removePlayer(Profile p) {
        players.remove(p);

        ChatMessage leftMessage = ChatMessages.LEFT_EVENT.clone().replace("%player%", p.getName());
        playerManager.broadcast(players, leftMessage);

        if (players.size() == 0) {
            eventManager.remove(this);
            ended = true;
            return;
        }

        if (started && players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromEvent();

            for (Profile pl : getSpectators()) {
                pl.stopSpectating();
            }

            eventManager.remove(this);
            ended = true;

            ChatMessage wonMessage = ChatMessages.WON_EVENT.clone().replace("%player%", winner.getName());

            playerManager.broadcast(playerManager.getProfiles(),
                    wonMessage);

            return;
        }
    }

    public void start() {

        if (started) {
            return;
        }

        ChatMessage messageToBroadcast = ChatMessages.BROADCAST_EVENT.clone()
                .replace("%player%", host).setTextEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + host),
                        ChatMessages.CLICK_TO_JOIN);

        playerManager.broadcast(playerManager.getProfiles(), messageToBroadcast);

        Event t = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                started = true;

                if (players.size() == 1) {
                    Profile winner = players.get(0);
                    winner.removeFromEvent();

                    for (Profile p : getSpectators()) {
                        p.stopSpectating();
                    }

                    ErrorMessages.EVENT_NOT_ENOUGH_PLAYERS.send(winner.bukkit());
                    eventManager.remove(t);
                    ended = true;
                    return;
                }

                startRound();
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 600);
    }

    public Arena getEventArena() {
        return eventArena;
    }

    public Location getWaitingLocation() {
        return loc;
    }

    public String getHost() {
        return host;
    }

    public void removeMatch(Match m) {
        matches.remove(m);

        if (ended) {
            return;
        }

        if (matches.isEmpty()) {
            ChatMessage broadcastedMessage = ChatMessages.ROUND_OVER.clone().replace("%round%", "" + round);

            playerManager.broadcast(players, broadcastedMessage);

            new BukkitRunnable() {
                @Override
                public void run() {
                    startRound();
                    round++;
                }
            }.runTaskLater(PracticePlugin.INSTANCE, 100);
        }
    }

    public boolean isEnded() {
        return ended;
    }

}
