package gg.mineral.practice.queue;

import java.util.List;

import gg.mineral.practice.entity.Profile;
import java.util.UUID;

public interface QueuedEntity {
    public List<Profile> getProfiles();

    public UUID getUuid();
}
