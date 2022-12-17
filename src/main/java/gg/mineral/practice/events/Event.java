package gg.mineral.practice.events;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.EventManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.EventMatch;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.traits.Spectatable;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;

public class Event implements Spectatable {

    GlueList<Match> matches = new GlueList<>();

    MatchData matchData;
    int round = 1;
    @Getter
    String host;
    @Getter
    boolean started = false, ended = false;
    ProfileList players = new ProfileList();
    @Getter
    Arena eventArena;

    public Event(Profile p, Arena eventArena) {
        this.matchData = p.getMatchData();
        this.host = p.getName();
        this.eventArena = eventArena;
        matchData.setArena(eventArena);
        addPlayer(p);
        EventManager.registerEvent(this);
    }

    public void addPlayer(Profile p) {

        if (started) {
            p.message(ErrorMessages.EVENT_STARTED);
            return;
        }

        PlayerUtil.teleport(p.getPlayer(), eventArena.getWaitingLocation());
        p.setEvent(this);
        players.add(p);

        ChatMessage joinedMessage = ChatMessages.JOINED_EVENT.clone().replace("%player%", p.getName());
        ProfileManager.broadcast(players, joinedMessage);
    }

    public void startRound() {

        if (players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromEvent();

            for (Profile p : getSpectators()) {
                p.stopSpectating();
            }

            ended = true;
            EventManager.remove(this);
            return;
        }

        Iterator<Profile> iter = players.iterator();

        Profile p1 = iter.next();

        if (!iter.hasNext()) {
            ChatMessages.NO_OPPONENT.send(p1.getPlayer());
            return;
        }

        Profile p2 = iter.next();

        EventMatch match = new EventMatch(p1, p2, matchData, this);
        match.start();
        matches.add(match);
    }

    public void removePlayer(Profile p) {
        players.remove(p);

        ChatMessage leftMessage = ChatMessages.LEFT_EVENT.clone().replace("%player%", p.getName());
        ProfileManager.broadcast(players, leftMessage);

        if (players.size() == 0) {
            EventManager.remove(this);
            ended = true;
            return;
        }

        if (started && players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromEvent();

            for (Profile pl : getSpectators()) {
                pl.stopSpectating();
            }

            EventManager.remove(this);
            ended = true;

            ChatMessage wonMessage = ChatMessages.WON_EVENT.clone().replace("%player%", winner.getName());

            ProfileManager.broadcast(ProfileManager.getProfiles(),
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

        ProfileManager.broadcast(ProfileManager.getProfiles(), messageToBroadcast);

        Event event = this;
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

                    ErrorMessages.EVENT_NOT_ENOUGH_PLAYERS.send(winner.getPlayer());
                    EventManager.remove(event);
                    ended = true;
                    return;
                }

                startRound();
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 600);
    }

    public void removeMatch(Match m) {
        matches.remove(m);

        if (ended) {
            return;
        }

        if (matches.isEmpty()) {
            ChatMessage broadcastedMessage = ChatMessages.ROUND_OVER.clone().replace("%round%", "" + round);

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
