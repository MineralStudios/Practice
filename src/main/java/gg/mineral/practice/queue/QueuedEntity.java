package gg.mineral.practice.queue;

import java.util.Queue;

import gg.mineral.practice.entity.Profile;
import java.util.UUID;

public interface QueuedEntity {
    Queue<Profile> getProfiles();

    UUID getUuid();

    QueueSettings getQueueSettings();
}
