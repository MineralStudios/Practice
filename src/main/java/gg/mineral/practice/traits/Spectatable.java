package gg.mineral.practice.traits;

import java.util.concurrent.ConcurrentLinkedDeque;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.collection.ProfileList;

public interface Spectatable {

    public ConcurrentLinkedDeque<Profile> getSpectators();

    public ProfileList getParticipants();

    public boolean isEnded();
}
