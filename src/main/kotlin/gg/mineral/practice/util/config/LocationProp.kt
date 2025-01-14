package gg.mineral.practice.util.config

import gg.mineral.api.config.FileConfiguration
import org.bukkit.Bukkit
import org.bukkit.Location

class LocationProp(config: FileConfiguration, path: String, default: Location) :
    CachedProp<Location>(config, path, default) {
    override fun readValue(): Location {
        val worldName = config.getString("$path.World", default.world.name)
        val world = Bukkit.getWorld(worldName)
        val x = config.getInt("$path.x", default.blockX)
        val y = config.getInt("$path.y", default.blockY)
        val z = config.getInt("$path.z", default.blockZ)
        val direction = config.getVector("$path.Direction", default.direction)
        val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
        location.direction = direction
        return location
    }

    override fun writeValue(value: Location) {
        config["$path.World"] = value.world.name
        config["$path.x"] = value.blockX
        config["$path.y"] = value.blockY
        config["$path.z"] = value.blockZ
        config["$path.Direction"] = value.direction
    }
}