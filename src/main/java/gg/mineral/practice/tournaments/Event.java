package gg.mineral.practice.tournaments;

import java.util.Iterator;

import gg.mineral.practice.arena.Arena;
import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.match.EventMatch;
import gg.mineral.practice.util.messages.ChatMessage;
import gg.mineral.practice.util.messages.impl.ChatMessages;
import gg.mineral.practice.util.messages.impl.ErrorMessages;

public class Event extends Tournament {

    Arena eventArena;

    public Event(Profile p, Arena eventArena) {
        super(p);
        this.eventArena = eventArena;
        m.setArena(eventArena);
        loc = eventArena.getWaitingLocation();
    }

    @Override
    public void addPlayer(Profile p) {

        if (started) {
            p.message(ErrorMessages.TOURNAMENT_STARTED);
            return;
        }

        p.teleport(loc);
        p.setTournament(this);
        players.add(p);

        ChatMessage joinedMessage = ChatMessages.JOINED_TOURNAMENT.clone().replace("%player%", p.getName());
        playerManager.broadcast(players, joinedMessage);
    }

    @Override
    public void startRound() {

        if (players.size() == 1) {
            Profile winner = players.get(0);
            winner.removeFromTournament();

            if (getSpectators().size() > 0) {
                for (Profile p : getSpectators()) {
                    p.stopSpectating();
                }
            }

            ended = true;
            tournamentManager.remove(this);
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

    public Arena getEventArena() {
        return eventArena;
    }

}
