package gg.mineral.practice.util.world

import org.bukkit.Material

data class BlockData(val location: BlockPosition, var type: Material, val data: Byte) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BlockData) return false

        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        return location.hashCode()
    }
}
