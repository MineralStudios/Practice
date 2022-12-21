package gg.mineral.practice.traits;

import java.util.concurrent.ConcurrentLinkedDeque;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.collection.ProfileList;

public interface Spectatable {

    ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<>();

    default ConcurrentLinkedDeque<Profile> getSpectators() {
        return spectators;
    }

    public ProfileList getParticipants();

    public boolean isEnded();
}
