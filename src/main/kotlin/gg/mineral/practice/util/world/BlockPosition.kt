package gg.mineral.practice.util.world

import org.bukkit.Location

open class BlockPosition(
    val blockX: Int,
    val blockY: Int,
    val blockZ: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockPosition) return false

        if (blockX != other.blockX) return false
        if (blockY != other.blockY) return false
        if (blockZ != other.blockZ) return false

        return true
    }

    override fun hashCode(): Int {
        var result = blockX
        result = 31 * result + blockY
        result = 31 * result + blockZ
        return result
    }

    fun isLocation(location: Location) = this.blockX == location.blockX &&
            this.blockY == location.blockY &&
            this.blockZ == location.blockZ
}