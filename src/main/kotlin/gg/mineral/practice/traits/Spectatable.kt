package gg.mineral.practice.traits

import gg.mineral.practice.entity.Profile
import gg.mineral.practice.util.collection.ProfileList
import org.bukkit.World
import java.util.concurrent.ConcurrentLinkedDeque

interface Spectatable {
    val spectators: ConcurrentLinkedDeque<Profile>

    val participants: ProfileList

    val ended: Boolean

    val world: World
}
