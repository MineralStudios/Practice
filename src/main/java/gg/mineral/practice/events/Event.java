package gg.mineral.practice.events;

import gg.mineral.api.collection.GlueList;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.bukkit.events.PlayerEventInitializeEvent;
import gg.mineral.practice.bukkit.events.PlayerEventStartEvent;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.managers.ArenaManager;
import gg.mineral.practice.managers.EventManager;
import gg.mineral.practice.managers.ProfileManager;
import gg.mineral.practice.match.EventMatch;
import gg.mineral.practice.match.Match;
import gg.mineral.practice.match.data.MatchData;
import gg.mineral.practice.traits.Spectatable;
import gg.mineral.practice.util.PlayerUtil;
import gg.mineral.practice.util.collection.ProfileList;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;
import lombok.Getter;
import lombok.val;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Event implements Spectatable {

    GlueList<Match> matches = new GlueList<>();
    @Getter
    ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<>();
    @Getter
    private final World world;
    MatchData matchData;
    int round = 1;
    @Getter
    private final String host;
    @Getter
    boolean started = false, ended = false;
    @Getter
    ProfileList participants = new ProfileList();
    @Getter
    byte eventArenaId;

    public Event(Profile p, byte eventArenaId) {
        val duelSettings = p.getDuelSettings();
        duelSettings.setArenaId(eventArenaId);
        this.matchData = new MatchData(duelSettings);
        this.host = p.getName();
        this.eventArenaId = eventArenaId;
        val arena = ArenaManager.getArenas().get(eventArenaId);
        this.world = arena.generate();
        addPlayer(p);
    }

    public void addPlayer(Profile p) {

        if (started) {
            p.message(ErrorMessages.EVENT_STARTED);
            return;
        }

        if (participants.contains(p)) {
            p.message(ErrorMessages.ALREADY_IN_EVENT);
            return;
        }

        val eventArena = ArenaManager.getArenas().get(eventArenaId);

        PlayerUtil.teleport(p, eventArena.getWaitingLocation().bukkit(this.world));
        p.setEvent(this);
        participants.add(p);

        val joinedMessage = ChatMessages.JOINED_EVENT.clone().replace("%player%", p.getName());
        ProfileManager.broadcast(participants, joinedMessage);
    }

    public void startRound() {

        if (participants.size() == 1) {
            val winner = participants.getFirst();
            winner.removeFromEvent();

            for (val spectator : getSpectators())
                spectator.getSpectateHandler().stopSpectating();

            ended = true;
            EventManager.remove(this);
            return;
        }

        val iter = participants.iterator();

        val p1 = iter.next();

        if (!iter.hasNext()) {
            ChatMessages.NO_OPPONENT.send(p1.getPlayer());
            return;
        }

        val p2 = iter.next();

        val match = new EventMatch(p1, p2, matchData, this);
        match.start();
        matches.add(match);
    }

    public void removePlayer(Profile p) {
        participants.remove(p);
        p.setEvent(null);

        val leftMessage = ChatMessages.LEFT_EVENT.clone().replace("%player%", p.getName());
        ProfileManager.broadcast(participants, leftMessage);

        if (participants.isEmpty()) {
            EventManager.remove(this);
            ended = true;
            return;
        }

        if (started && participants.size() == 1) {
            val winner = participants.getFirst();
            winner.removeFromEvent();

            for (val spectator : getSpectators())
                spectator.getSpectateHandler().stopSpectating();

            EventManager.remove(this);
            ended = true;

            val wonMessage = ChatMessages.WON_EVENT.clone().replace("%player%", winner.getName());

            ProfileManager.broadcast(wonMessage);

        }
    }

    public void start() {

        if (started)
            return;

        val bukkitHost = participants.getFirst().getPlayer();

        val event = new PlayerEventInitializeEvent(30, bukkitHost);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        EventManager.registerEvent(this);

        val messageToBroadcast = ChatMessages.BROADCAST_EVENT.clone()
                .replace("%player%", host).setTextEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + host),
                        ChatMessages.CLICK_TO_JOIN);

        ProfileManager.broadcast(messageToBroadcast);

        new BukkitRunnable() {
            @Override
            public void run() {
                started = true;

                PlayerEventStartEvent event = new PlayerEventStartEvent(bukkitHost);
                Bukkit.getPluginManager().callEvent(event);

                if (participants.size() == 1 || event.isCancelled()) {
                    val winner = participants.getFirst();
                    winner.removeFromEvent();

                    for (val spectator : getSpectators())
                        spectator.getSpectateHandler().stopSpectating();

                    ErrorMessages.EVENT_NOT_ENOUGH_PLAYERS.send(winner.getPlayer());
                    EventManager.remove(Event.this);
                    ended = true;
                    return;
                }

                startRound();
            }
        }.runTaskLater(PracticePlugin.INSTANCE, 600);
    }

    public void removeMatch(Match m) {
        matches.remove(m);

        if (ended)
            return;

        if (matches.isEmpty()) {
            val broadcastedMessage = ChatMessages.ROUND_OVER.clone().replace("%round%", "" + round);

            ProfileManager.broadcast(participants, broadcastedMessage);

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
