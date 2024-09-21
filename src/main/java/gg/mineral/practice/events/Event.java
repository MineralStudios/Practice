package gg.mineral.practice.events;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import gg.mineral.api.collection.GlueList;
import gg.mineral.bot.api.BotAPI;
import gg.mineral.practice.PracticePlugin;
import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.bukkit.events.PlayerEventInitializeEvent;
import gg.mineral.practice.bukkit.events.PlayerEventStartEvent;
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

    GlueList<Match<MatchData>> matches = new GlueList<>();
    @Getter
    ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<>();

    MatchData matchData;
    int round = 1;
    @Getter
    String host;
    @Getter
    boolean started = false, ended = false;
    @Getter
    ProfileList participants = new ProfileList();
    @Getter
    Arena eventArena;

    public Event(Profile p, Arena eventArena) {
        this.matchData = p.getMatchData();
        this.host = p.getName();
        this.eventArena = eventArena;
        matchData.setArena(eventArena);
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

        PlayerUtil.teleport(p.getPlayer(), eventArena.getWaitingLocation());
        p.setEvent(this);
        participants.add(p);

        ChatMessage joinedMessage = ChatMessages.JOINED_EVENT.clone().replace("%player%", p.getName());
        ProfileManager.broadcast(participants, joinedMessage);
    }

    public void startRound() {

        if (participants.size() == 1) {
            Profile winner = participants.getFirst();
            winner.removeFromEvent();

            for (Profile spectator : getSpectators()) {
                spectator.getSpectateHandler().stopSpectating();
            }

            ended = true;
            EventManager.remove(this);
            return;
        }

        Iterator<Profile> iter = participants.iterator();

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
        participants.remove(p);
        p.setEvent(null);

        ChatMessage leftMessage = ChatMessages.LEFT_EVENT.clone().replace("%player%", p.getName());
        ProfileManager.broadcast(participants, leftMessage);

        if (participants.size() == 0) {
            EventManager.remove(this);
            ended = true;
            return;
        }

        if (started && participants.size() == 1) {
            Profile winner = participants.getFirst();
            winner.removeFromEvent();

            for (Profile spectator : getSpectators())
                spectator.getSpectateHandler().stopSpectating();

            EventManager.remove(this);
            ended = true;

            ChatMessage wonMessage = ChatMessages.WON_EVENT.clone().replace("%player%", winner.getName());

            ProfileManager.broadcast(wonMessage);

            return;
        }
    }

    public void start() {

        if (started)
            return;

        final Player bukkitHost = participants.getFirst().getPlayer();

        PlayerEventInitializeEvent event = new PlayerEventInitializeEvent(30, bukkitHost);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        EventManager.registerEvent(this);

        ChatMessage messageToBroadcast = ChatMessages.BROADCAST_EVENT.clone()
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
                    Profile winner = participants.getFirst();
                    winner.removeFromEvent();

                    for (Profile spectator : getSpectators())
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

    public void removeMatch(Match<MatchData> m) {
        matches.remove(m);

        if (ended) {
            return;
        }

        if (matches.isEmpty()) {
            ChatMessage broadcastedMessage = ChatMessages.ROUND_OVER.clone().replace("%round%", "" + round);

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

    public void updateVisiblity(Event event, Profile profile) {
        boolean isSpectator = event.getSpectators().contains(profile),
                isParticipant = event.getParticipants().contains(profile);
        if (isParticipant || isSpectator) {
            for (Profile participant : participants) {
                if (isParticipant && !isSpectator)
                    participant.getPlayer().showPlayer(profile.getPlayer());
                else
                    participant.getPlayer().hidePlayer(profile.getPlayer(),
                            BotAPI.INSTANCE.isFakePlayer(profile.getUuid()));
                profile.getPlayer().showPlayer(participant.getPlayer());
            }
        } else {
            boolean isFakePlayer = BotAPI.INSTANCE.isFakePlayer(profile.getUuid());
            for (Profile participant : isFakePlayer
                    ? ProfileManager.getProfiles().values()
                    : participants) {

                if (isFakePlayer && this.participants.contains(participant))
                    continue;
                participant.getPlayer().hidePlayer(profile.getPlayer(), isFakePlayer);
                profile.getPlayer().hidePlayer(participant.getPlayer(), isFakePlayer);
            }
        }
    }
}
