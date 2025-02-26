package gg.mineral.practice.traits

import gg.mineral.practice.util.collection.ProfileList
import org.bukkit.World
import java.lang.ref.WeakReference

interface Spectatable {
    val spectators: ProfileList

    val participants: ProfileList

    val ended: Boolean

    val world: WeakReference<World>
}
