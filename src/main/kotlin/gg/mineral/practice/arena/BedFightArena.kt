package gg.mineral.practice.arena

import gg.mineral.practice.util.world.SpawnLocation

interface BedFightArena : BuildArena {
    /**
     * If this arena is a bed fight arena.
     */
    var bedFightArena: Boolean

    /**
     * The first bed location of the arena.
     */
    var bedLocation1Head: SpawnLocation
    var bedLocation1Foot: SpawnLocation

    /**
     * The second bed location of the arena.
     */
    var bedLocation2Head: SpawnLocation
    var bedLocation2Foot: SpawnLocation
}