package gg.mineral.practice.traits;

import java.util.concurrent.ConcurrentLinkedDeque;

import gg.mineral.practice.entity.Spectator;
import gg.mineral.practice.util.collection.ProfileList;

public interface Spectatable {
    ConcurrentLinkedDeque<Spectator> spectators = new ConcurrentLinkedDeque<>();

    default ConcurrentLinkedDeque<Spectator> getSpectators() {
        return spectators;
    }

    public ProfileList getParticipants();

    public boolean isEnded();
}
