package gg.mineral.practice.util.world

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

data class BlockData(private val location: Location, private var type: Material, private val data: Byte) {
    fun clone() = BlockData(location.clone(), type, data)

    fun setType(type: Material): BlockData {
        this.type = type
        return this
    }

    fun translate(x: Int, y: Int, z: Int): BlockData {
        location.add(x.toDouble(), y.toDouble(), z.toDouble())
        return this
    }

    fun update(player: Player) = player.sendBlockChange(location, type, data)

    fun remove(player: Player) {
        val block = location.block
        player.sendBlockChange(location, block.type, block.data)
    }
}
