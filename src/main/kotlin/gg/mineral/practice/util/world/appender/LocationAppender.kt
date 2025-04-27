package gg.mineral.practice.util.world.appender

import gg.mineral.practice.util.world.SpawnLocation
import org.bukkit.Location

interface LocationAppender {
    fun Location.toSpawnLocation(): SpawnLocation {
        val spawnLocation = SpawnLocation(blockX, blockY, blockZ)
        spawnLocation.direction = direction
        return spawnLocation
    }
}