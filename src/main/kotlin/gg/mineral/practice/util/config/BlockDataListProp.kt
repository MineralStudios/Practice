package gg.mineral.practice.util.config

import gg.mineral.practice.util.config.yaml.FileConfiguration
import gg.mineral.practice.util.world.BlockData
import gg.mineral.practice.util.world.BlockPosition
import org.bukkit.Material

class BlockDataListProp(
    config: FileConfiguration,
    path: String,
    default: List<BlockData>
) : CachedProp<List<BlockData>>(config, path, default) {

    override fun readValue(): List<BlockData> {
        val raw = config.bukkitConfig().getMapList(path)
        if (raw.isEmpty()) return default

        return raw.map { m ->
            val x = (m["x"] as Number).toInt()
            val y = (m["y"] as Number).toInt()
            val z = (m["z"] as Number).toInt()
            val mat = Material.valueOf(m["type"] as String)
            val data = (m["data"] as Number).toByte()
            BlockData(BlockPosition(x, y, z), mat, data)
        }
    }

    override fun writeValue(value: List<BlockData>) {
        val serialized = value.map { bd ->
            mapOf(
                "x" to bd.location.blockX,
                "y" to bd.location.blockY,
                "z" to bd.location.blockZ,
                "type" to bd.type.name,
                "data" to bd.data.toInt()
            )
        }
        config[path] = serialized
    }
}

