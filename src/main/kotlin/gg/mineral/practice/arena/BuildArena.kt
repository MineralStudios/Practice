package gg.mineral.practice.arena

import gg.mineral.practice.util.world.BlockData

interface BuildArena : Arena {
    /**
     *  The breakable block locations of the arena.
     */
    var breakableBlockLocations: List<BlockData>
}