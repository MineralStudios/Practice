package gg.mineral.practice.queue

import gg.mineral.practice.entity.Profile
import java.util.*

interface QueuedEntity {
    val profiles: Queue<Profile>

    val uuid: UUID?

    val queueSettings: QueueSettings
}
