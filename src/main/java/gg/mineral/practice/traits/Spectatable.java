package gg.mineral.practice.traits;

import gg.mineral.practice.entity.Profile;
import gg.mineral.practice.util.collection.ProfileList;
import org.bukkit.World;

import java.util.concurrent.ConcurrentLinkedDeque;

public interface Spectatable {

    ConcurrentLinkedDeque<Profile> getSpectators();

    ProfileList getParticipants();

    boolean isEnded();

    World getWorld();
}
