package gg.mineral.practice.util.world.appender

import gg.mineral.practice.util.world.BlockPosition
import gg.mineral.practice.util.world.SpawnLocation
import org.bukkit.Location

fun Location.toSpawnLocation(): SpawnLocation {
    val spawnLocation = SpawnLocation(blockX, blockY, blockZ)
    spawnLocation.direction = direction
    return spawnLocation
}

fun Location.toBlockPosition(): BlockPosition {
    return BlockPosition(blockX, blockY, blockZ)
}