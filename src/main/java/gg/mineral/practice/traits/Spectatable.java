package gg.mineral.practice.traits;

import java.util.concurrent.ConcurrentLinkedDeque;

import gg.mineral.practice.entity.Profile;

public interface Spectatable {
    ConcurrentLinkedDeque<Profile> spectators = new ConcurrentLinkedDeque<>();

    default ConcurrentLinkedDeque<Profile> getSpectators() {
        return spectators;
    }
}
