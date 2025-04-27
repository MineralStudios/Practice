package gg.mineral.practice.arena

import gg.mineral.practice.util.world.SpawnLocation

interface EventArena : Arena {
    /**
     * The waiting location of the arena.
     */
    var waitingLocation: SpawnLocation
}