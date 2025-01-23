package gg.mineral.practice.util.config

import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.world.SpawnLocation

class SpawnLocationProp(config: FileConfiguration, path: String, default: SpawnLocation) :
    CachedProp<SpawnLocation>(config, path, default) {
    override fun readValue(): SpawnLocation {
        val x = config.getInt("$path.x", default.blockX)
        val y = config.getInt("$path.y", default.blockY)
        val z = config.getInt("$path.z", default.blockZ)
        val direction = config.getVector("$path.Direction", default.direction)
        val spawnLocation = SpawnLocation(x, y, z)
        spawnLocation.direction = direction
        return spawnLocation
    }

    override fun writeValue(value: SpawnLocation) {
        config["$path.x"] = value.blockX
        config["$path.y"] = value.blockY
        config["$path.z"] = value.blockZ
        config["$path.Direction"] = value.direction
    }
}